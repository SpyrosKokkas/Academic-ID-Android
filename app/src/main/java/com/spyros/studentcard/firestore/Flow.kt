package com.spyros.studentcard.firestore

import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


// Η συνάρτηση αυτή χρησιμοποιεί τη μέθοδο asFlow για να μετατρέψει ένα Firestore Query σε Kotlin Flow
// με την δυνατότητα να βλέπουμε τις αλλαγές σε πραγματικό χρόνο
fun Query.asFlow(): Flow<List<Map<String, Any>>> = callbackFlow {
    val listenerRegistration = addSnapshotListener { snapshot, error ->
        if (error != null) {
            close(error)
            return@addSnapshotListener
        }
        val documents = snapshot?.documents?.map { it.data.orEmpty() } ?: emptyList()
        trySend(documents).isSuccess
    }
    awaitClose { listenerRegistration.remove() }
}