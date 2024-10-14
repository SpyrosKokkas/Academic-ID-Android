package com.spyros.studentcard

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.spyros.studentcard.composeAssets.StudentCard
import com.spyros.studentcard.ui.theme.StudentCardTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ViewStudentCard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Λήψει των στοιχείων του φοιτητή απο τη προηγούμενη οθόνη
        val name = intent.getStringExtra("name") ?: ""
        val surname = intent.getStringExtra("surname") ?: ""
        val address = intent.getStringExtra("address") ?: ""
        val imageId = intent.getStringExtra("imageId") ?: ""
        val regDate = intent.getStringExtra("regDate") ?: ""
        val idNumber = intent.getStringExtra("idNumber") ?: ""
        val ticket = intent.getStringExtra("ticket") ?: ""
        val uni = intent.getStringExtra("uni") ?: ""
        val regNumber = intent.getStringExtra("regNumber") ?: ""
        val attribute = intent.getStringExtra("attribute") ?: ""

        val uniImage = addUniImage(uni)

        val storage = Firebase.storage
        val storageRef = storage.getReferenceFromUrl(imageId)

        storageRef.downloadUrl.addOnSuccessListener { uri ->
            val imageUrl = uri.toString()
            setContent {
                StudentCardTheme {
                    StudentCard(
                        // Μεταφορά των στοιχείων του φοιτητή στην οθόνη προβολής
                        name = name,
                        surname = surname,
                        address = address,
                        imageId = imageUrl,
                        regDate = regDate,
                        idNumber = idNumber,
                        ticket = ticket,
                        uni = uni,
                        uniImage = uniImage,
                        regNumber = regNumber,
                        attribute = attribute,
                        context = this@ViewStudentCard,
                        returnBtn = { returnBtn , context ->
                            if (returnBtn) {
                                returnBtnFun(context)
                            }
                        }
                    )
                }
            }
        }.addOnFailureListener {
            // Handle any errors
        }
    }

    // Προσθήκη της κατάλληλης εικόνα με βάση το πανεπιστήμιο του φοιτητή
    private fun addUniImage(uni: String): Int {
        return if (uni == "Πανεπιστήμιο Πειραιώς") {
            R.drawable.unipi_logo
        } else {
            R.drawable.uni_png_cap
        }
    }

    // Κουμπί επιστροφής στην οθόνη αναζήτησης με ταυτόχρονή διαγραφή των preferences απο το DataStore
    // Δηλαδή το αποθηκευμένο πάσο διαγράφεται.
    private fun returnBtnFun(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit { preferences ->
                preferences.clear()
            }
        }
        val intent = Intent(this, MainPage::class.java)
        startActivity(intent)
        finish()
    }
}
