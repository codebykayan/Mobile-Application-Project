package com.example.tinystep_stage2

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import androidx.core.content.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Locale

object LanguageManager {
    fun getLanguage(context: Context): String {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        return prefs.getString("app_language", "en") ?: "en"
    }

    fun setLanguage(context: Context, languageCode: String) {
        val prefs = context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
        prefs.edit { putString("app_language", languageCode) }

        syncLanguageToFirestore(languageCode)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val localeManager = context.getSystemService(LocaleManager::class.java)
            localeManager?.applicationLocales = LocaleList.forLanguageTags(languageCode)
        } else {
            val locale = Locale.forLanguageTag(languageCode)
            Locale.setDefault(locale)
            val resources = context.resources
            val config = Configuration(resources.configuration)
            config.setLocale(locale)
            @Suppress("DEPRECATION")
            resources.updateConfiguration(config, resources.displayMetrics)
        }

        (context as? Activity)?.recreate()
    }

    fun applyLanguage(context: Context): Context {
        val languageCode = getLanguage(context)
        val locale = Locale.forLanguageTag(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        return context.createConfigurationContext(config)
    }

    private fun syncLanguageToFirestore(languageCode: String) {
        try {
            val auth = FirebaseAuth.getInstance()
            val uid = auth.currentUser?.uid
            if (uid != null) {
                FirebaseFirestore.getInstance()
                    .collection("parents")
                    .document(uid)
                    .set(mapOf("language" to languageCode), SetOptions.merge())
            }
        } catch (_: Exception) {
            // Handled gracefully when Firebase is uninitialized in offline/demo mode
        }
    }
}
