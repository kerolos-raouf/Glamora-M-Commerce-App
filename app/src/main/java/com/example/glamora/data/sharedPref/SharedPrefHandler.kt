package com.example.glamora.data.sharedPref

import android.content.SharedPreferences
import com.example.glamora.data.contracts.SettingsHandler
import javax.inject.Inject

class SharedPrefHandler @Inject constructor(
    private val sharedPreferences: SharedPreferences
) : SettingsHandler{
    override fun setSharedPrefString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getSharedPrefString(key: String, defaultValue: String) : String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun setSharedPrefBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    override fun getSharedPrefBoolean(key: String, defaultValue: Boolean) : Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }
}