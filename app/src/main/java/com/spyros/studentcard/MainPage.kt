package com.spyros.studentcard

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.spyros.studentcard.composeAssets.SearchId
import com.spyros.studentcard.firestore.asFlow
import kotlinx.coroutines.launch

class MainPage : ComponentActivity() {

    // Μεταβλητή για την αποθήκευση του αποτελέσματος του QR code
    private val qrResult = mutableStateOf("")

    // Συνάρτηση για την εμφάνιση της κάμερας και τη σάρωση του QR code
    private fun showCamera(){
        val options = ScanOptions()
        options.setDesiredBarcodeFormats(ScanOptions.QR_CODE)
        options.setPrompt("Scan a QR Code")
        options.setCameraId(0)
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)

        barCodeLauncher.launch(options)
    }

    //Για τα αποτελέσματα της σάρωσης του QR code
    private val barCodeLauncher = registerForActivityResult(ScanContract()){
            result ->
        if (result.contents == null){
            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
        } else {
            qrResult.value = result.contents
            Toast.makeText(this, result.contents, Toast.LENGTH_SHORT).show()
            Log.d("MainPage", "QR Result: ${qrResult.value}")
            searchByQr(qrResult.value)
        }
    }

    // Έλεγχος άδειας χρήσης κάμερας. Εάν δεν έχει δωθεί η άδεια τότε τη ζητάει.
    private val requestPermissionsLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            isGranted: Boolean ->
        if (isGranted) {
            showCamera()
        }
    }

    // Κλειδιά για την αποθήκευση των preferences στο DataStore
    private val SAVED_KEY = booleanPreferencesKey("saved")
    private val REG_NUMBER_KEY = stringPreferencesKey("regNumber")
    private val LAST_NAME_KEY = stringPreferencesKey("lastName")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchId(
                searchById = { regNumber, lastName -> searchById(regNumber, lastName) },
                saveId = { save, regNumberSvd, lastNameSvd ->
                    lifecycleScope.launch {
                        saveId(save, regNumberSvd, lastNameSvd)
                    }
                },
                onQrCodeClicked = { clicked ->
                    if (clicked) {
                        checkCameraPermission(this@MainPage)
                    }
                },
                context = this@MainPage,
                onFinishActivity = { finish() }
            )
        }
    }

    // Συνάρτηση για τον έλεγχο της άδειας χρήσης της κάμερας
    private fun checkCameraPermission(context: Context) {
        if (ContextCompat.checkSelfPermission(context,
                android.Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED) {
            showCamera()
        } else if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            Toast.makeText(context, "Camera permission is required to scan QR codes", Toast.LENGTH_SHORT).show()
        } else {
            requestPermissionsLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    // Συνάρτηση για την αποθήκευση των στοιχείων ταυτότητας στο DataStore
    private suspend fun saveId(save: Boolean, regNumberSvd: String, lastNameSvd: String) {
        if (save) {
            saveUserID(this, regNumberSvd, lastNameSvd, save)
        }
    }

    // Συνάρτηση για την αναζήτηση φοιτητή με βάση τον αριθμό μητρώου και το επώνυμο
    private fun searchById(regNumber: String, lastName: String) {
        val db = Firebase.firestore
        val studentId = db.collection("Student_Ids")
            .whereEqualTo("regNumber", regNumber)
            .whereEqualTo("Surname", lastName)

        lifecycleScope.launch {
            studentId.asFlow()
                .collect { documents ->
                    if (documents.isEmpty()) {
                        Toast.makeText(this@MainPage, "Δεν βρέθηκαν αποτελέσματα", Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in documents) {
                            println("Document Data: $document")

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

                            val intent = Intent(this@MainPage, ViewStudentCard::class.java).apply {
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

                            startActivity(intent)
                            finish()
                        }
                    }
                }
        }
    }

    // Συνάρτηση για την αναζήτηση φοιτητή με βάση το αποτέλεσμα του QR code
    private fun searchByQr(qrResult: String) {
        Log.d("MainPage", "searchByQr called with qrResult: $qrResult")
        val db = Firebase.firestore
        val studentId = db.collection("Student_Ids")
            .whereEqualTo("idNumber", qrResult)

        lifecycleScope.launch {
            studentId.asFlow()
                .collect { documents ->
                    if (documents.isEmpty()) {
                        Toast.makeText(this@MainPage, "Δεν βρέθηκαν αποτελέσματα", Toast.LENGTH_SHORT).show()
                    } else {
                        for (document in documents) {
                            println("Document Data: $document")
                            val surname = document["Surname"] as? String
                            val regNumber = document["regNumber"] as? String

                            if (surname != null && regNumber != null) {
                                searchById(regNumber, surname)
                            } else {
                                Log.d("MainPage", "No matching document found for qrResult: $qrResult")
                            }
                        }
                    }
                }
        }
    }

    // Συνάρτηση για την αποθήκευση των στοιχείων ταυτότητας στο DataStore
    private suspend fun saveUserID(context: Context, regNumber: String, lastName: String, save: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REG_NUMBER_KEY] = regNumber
            preferences[LAST_NAME_KEY] = lastName
            preferences[SAVED_KEY] = save
        }
    }
}
