package com.spyros.studentcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.spyros.studentcard.firestore.asFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

//Η οθόνη έναρξης της εφαρμογής (Splash Screen )

class SplashActivity : ComponentActivity() {

    //Τα κλειδιά αποθήκευσης των δεδομένω στο DataStore
    private val SAVED_KEY = booleanPreferencesKey("saved")
    private val REG_NUMBER_KEY = stringPreferencesKey("regNumber")
    private val LAST_NAME_KEY = stringPreferencesKey("lastName")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen {
                //Κατα την έναρξη της οθόνης, ελέγχει εάν ο χρήστης είναι συνδεδεμένος ή όχι
                checkAuthenticationStatus()
            }
        }


        // Γίνεται έλεγχος εάν ο χρήστης έχει αποθηκευτεί το πάσο του
        lifecycleScope.launch {
            val (regNumber, lastName, saved) = getUserID(this@SplashActivity)
            Log.d("MainPage", "Retrieved data - regNumber: $regNumber, lastName: $lastName, saved: $saved")

            if (saved == true) {
                regNumber?.let { reg ->
                    lastName?.let { last ->
                        Log.d("MainPage", "Performing search with - regNumber: $reg, lastName: $last")
                        searchById(reg, last)
                    }
                }
            }
        }


    }

    // Έλεγχος εάν είναι συνδεδεμένος ο χρήστης ή όχι
    private fun checkAuthenticationStatus() {
        val user = Firebase.auth.currentUser
        if (user != null) {
            // Ο χρήστης έιναι συνδεδεμένος , μεταφορά στην σελίδα αναζήτησης
            val intent = Intent(this, MainPage::class.java)
            startActivity(intent)
            finish()
        } else {
            // Ο χρήστης δεν είναι συνδεδεμένος , μεταφορά στην σελίδα εισόδου
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    // Τραβάει τα δεδομένα απο το preferences DataStore
    private suspend fun getUserID(context: Context): Triple<String?, String?, Boolean?> {
        val preferences = context.dataStore.data.first()
        val regNumber = preferences[REG_NUMBER_KEY]
        val lastName = preferences[LAST_NAME_KEY]
        val saved = preferences[SAVED_KEY]

        return Triple(regNumber, lastName, saved)
    }


    // Ελέγχει εάν υπάρχει πάσο με τα στοιχεία απο το dataStore
    private fun searchById(regNumber: String, lastName: String) {
        val db = Firebase.firestore
        val studentId = db.collection("Student_Ids")
            .whereEqualTo("regNumber", regNumber)
            .whereEqualTo("Surname", lastName)

        lifecycleScope.launch {
            studentId.asFlow()
                .collect { documents ->
                    for (document in documents) {
                        println("Document Data: $document")

                        //Τραβάει όλα τα δεδομένα απο τη βάση δεδομένων που ταιριάζουν με αυτά της αναζήτησης
                        val name = document["Name"] as? String
                        val surname = document["Surname"] as? String
                        val address = document["address"] as? String
                        val imageId = document["imageId"] as? String
                        val regDate = document["regDate"] as? String
                        val idNumber = document["idNumber"] as? String
                        val ticket = document["ticket"] as? String
                        val uni = document["university"] as? String
                        val regNumber = document["regNumber"] as? String
                        val attribute = document["attribute"] as? String

                        //Περνάει όλα τα δεδομένα που τράβηξε στην επόμενη σελίδα για την εμφάνιση του πάσου
                        val intent = Intent(this@SplashActivity, ViewStudentCard::class.java).apply {
                            putExtra("name", name)
                            putExtra("surname", surname)
                            putExtra("address", address)
                            putExtra("imageId", imageId)
                            putExtra("regDate", regDate)
                            putExtra("idNumber", idNumber)
                            putExtra("ticket", ticket)
                            putExtra("uni", uni)
                            putExtra("regNumber", regNumber)
                            putExtra("attribute", attribute)
                        }

                        //Κάνει έναρξη της επόμενης οθόνης και κλείνει την τρέχουσα
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }

}


// Το composable της οθόνης έναρξης της εφαρμογής
@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val brush = Brush.verticalGradient(
                    colors = listOf(Color.White, Color(0xFF112693)),
                    startY = 0f,
                    endY = size.height
                )
                drawRect(brush = brush)
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.uni_png_cap),
                contentDescription = null,
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Ακαδημαϊκή Ταυτότητα",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }

    LaunchedEffect(Unit) {
        delay(3000)
        onTimeout()
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen{}
}
