package com.spyros.studentcard


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

// Μετατρέπει το Preferences DataStore σε singleton για την αποθήκευση των στοιχείων του χρήστη
// και την αποφυγεί σφαλμάτων διπλότυπων DataStore

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")