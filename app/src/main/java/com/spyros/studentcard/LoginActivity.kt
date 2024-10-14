package com.spyros.studentcard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.spyros.studentcard.composeAssets.LoginPage
import com.spyros.studentcard.firestore.FirestoreRoleSearch

class LoginActivity : ComponentActivity() {

    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            /* Χρήσιμοποιεί τις μεθόδους onLogin και onRegister μαζι με τα δεδομένα που σταλθηκαν απο το
                composable για να κάνει σύνδεση ή εγγραφή ο χρήστης
             */
            LoginPage(
                onLogin = { username, password -> handleLogin(username, password) },
                onRegister = { username, password -> handleRegister(username, password) }
            )
        }
    }

    // Η συνάρτηση αυτή διαχειρίζεται τη σύνδεση του χρήστη.
    private fun handleLogin(username: String, password: String) {
        // Εάν τα πεδία είναι συμπληρωμένα σωστά τότε προσπαθεί να πραγματοποιήσει την σύνδεση
        if (fieldsCheck(username, password)) {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginActivity", "signInWithEmail:success")
                        val user = auth.currentUser
                        if (user != null) {
                            val firestoreRoleSearch = FirestoreRoleSearch()
                            firestoreRoleSearch.userRole(user.uid, { role ->
                                when (role) {
                                    "user" -> {
                                        Log.d("LoginActivity", "current user is $user")
                                        Log.d("LoginActivity", "current user role is $role")
                                        val intent = Intent(this, MainPage::class.java)
                                        startActivity(intent)
                                        finish()
                                    }
                                    else -> {
                                        Log.w("LoginActivity", "Unknown role: $role")
                                        Toast.makeText(baseContext, "Role not recognized.",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }, { exception ->
                                Log.w("LoginActivity", "Failed to get user role", exception)
                                Toast.makeText(baseContext, "Failed to get user role.",
                                    Toast.LENGTH_SHORT).show()
                            })
                        }
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    // Η συνάρτηση αυτή διαχειρίζεται την εγγραφή του χρήστη.
    private fun handleRegister(username: String, password: String) {
        // Εάν τα πεδία είναι συμπληρωμένα σωστά τότε προσπαθεί να πραγματοποιήσει την εγγραφή
        if (fieldsCheck(username, password)) {
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Log.d("LoginActivity", "createUserWithEmail:success")
                        val intent = Intent(this, MainPage::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Log.w("LoginActivity", "createUserWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Registration failed.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    //Η συνάρτηση αυτή ελέγχει ότι τα πεδία είναι συμπληρωμένα σωστά
    private fun fieldsCheck(username: String, password: String): Boolean {
        var valid = true
        if (username.isEmpty()) {
            Toast.makeText(this, "Username cannot be empty", Toast.LENGTH_SHORT).show()
            valid = false
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty", Toast.LENGTH_SHORT).show()
            valid = false
        }
        if (password.length < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            valid = false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
            valid = false
        }
        return valid
    }
}
