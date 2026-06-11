package com.eloop.mobileapp.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SessionManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "ELoopSession"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_USER_EMAIL = "userEmail"
        private const val KEY_USER_NAME = "userName"
        private const val KEY_THEME_MODE = "themeMode" // "SYSTEM", "LIGHT", "DARK"
        private const val KEY_2FA_ENABLED = "2faEnabled"
        private const val KEY_PUSH_ENABLED = "pushEnabled"
        private const val KEY_EMAIL_ENABLED = "emailEnabled"
        private const val KEY_PROMO_ENABLED = "promoEnabled"
        private const val KEY_APP_LANGUAGE = "appLanguage" // "EN", "AR"
    }

    fun setThemeMode(mode: String) {
        prefs.edit { putString(KEY_THEME_MODE,  mode) }
    }

    fun getThemeMode(): String = prefs.getString(KEY_THEME_MODE, "SYSTEM") ?: "SYSTEM"

    fun setAppLanguage(lang: String) {
        prefs.edit { putString(KEY_APP_LANGUAGE,  lang) }
    }

    fun getAppLanguage(): String {
        val savedLang = prefs.getString(KEY_APP_LANGUAGE, null)
        if (savedLang != null) return savedLang
        
        // Fallback to system default if not set
        val currentLang = java.util.Locale.getDefault().language
        return if (currentLang == "ar") "AR" else "EN"
    }

    // Settings Getters & Setters
    var is2FAEnabled: Boolean
        get() = prefs.getBoolean(KEY_2FA_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_2FA_ENABLED,  value) }

    var isPushEnabled: Boolean
        get() = prefs.getBoolean(KEY_PUSH_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_PUSH_ENABLED,  value) }

    var isEmailEnabled: Boolean
        get() = prefs.getBoolean(KEY_EMAIL_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_EMAIL_ENABLED,  value) }

    var isPromoEnabled: Boolean
        get() = prefs.getBoolean(KEY_PROMO_ENABLED, false)
        set(value) = prefs.edit { putBoolean(KEY_PROMO_ENABLED,  value) }

    fun setLoggedIn(isLoggedIn: Boolean, email: String = "", name: String = "") {
        prefs.edit {
            putBoolean(KEY_IS_LOGGED_IN, isLoggedIn)
            putString(KEY_USER_EMAIL, email)
            putString(KEY_USER_NAME, name)
        }
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun getUserEmail(): String = prefs.getString(KEY_USER_EMAIL, "") ?: ""

    fun getUserName(): String = prefs.getString(KEY_USER_NAME, "") ?: ""

    fun logout() {
        prefs.edit { clear() }
    }
}

