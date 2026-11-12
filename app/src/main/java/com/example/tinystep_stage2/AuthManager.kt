package com.example.tinystep_stage2

import android.content.Context
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth

object AuthManager {
    private const val PREFS_NAME = "auth_prefs"
    private const val KEY_REMEMBER_ME = "remember_me"

    fun isRememberMeEnabled(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_REMEMBER_ME, false)
    }

    fun setRememberMeEnabled(context: Context, enabled: Boolean) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit { putBoolean(KEY_REMEMBER_ME, enabled) }
    }

    fun getFirebaseAuth(): FirebaseAuth? {
        return try {
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    fun isUserSignedInOnLaunch(context: Context): Boolean {
        val rememberMe = isRememberMeEnabled(context)
        if (!rememberMe) {
            // Requirement 3: When Remember Me is disabled, require authentication again on a new app launch
            getFirebaseAuth()?.signOut()
            return false
        }
        val currentUser = getFirebaseAuth()?.currentUser
        return currentUser != null
    }

    fun signOut(context: Context) {
        setRememberMeEnabled(context, enabled = false)
        try {
            getFirebaseAuth()?.signOut()
        } catch (_: Exception) {
        }
    }
}
