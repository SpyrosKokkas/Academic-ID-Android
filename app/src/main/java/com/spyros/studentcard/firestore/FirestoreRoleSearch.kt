package com.spyros.studentcard.firestore

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirestoreRoleSearch {
    // Κάνει αναζήτηση κατα την είσοδο του χρήστη για να βρεί το ρόλο του, δηλαδή εάν είναι απλός χρήστης
    // ή αν είναι διαχειριστής
    // Η δυνατότητα διαχειριστή δεν έχει υλοποιηθεί

    fun userRole(userID: String, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
        val db = Firebase.firestore
        val users = db.collection("Users")
        val docRef = users.document(userID)

        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val role = document.getString("role") ?: "user"
                    onSuccess(role)
                } else {
                    Log.d("FireStoreRoleSearch", "No such document")
                    onSuccess("user") 
                }
            }
            .addOnFailureListener { exception ->
                Log.d("FireStoreRoleSearch", "get failed with ", exception)
                onFailure(exception)
            }
    }
}