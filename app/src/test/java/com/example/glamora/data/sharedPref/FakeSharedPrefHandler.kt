package com.example.glamora.data.sharedPref

import android.content.SharedPreferences
import com.example.glamora.data.contracts.SettingsHandler

class FakeSharedPrefHandler : SettingsHandler {
    override fun setSharedPrefString(key: String, value: String) {
        TODO("Not yet implemented")
    }

    override fun getSharedPrefString(key: String, defaultValue: String): String {
        TODO("Not yet implemented")
    }

    override fun setSharedPrefBoolean(key: String, value: Boolean) {
        TODO("Not yet implemented")
    }

    override fun getSharedPrefBoolean(key: String, defaultValue: Boolean): Boolean {
        TODO("Not yet implemented")
    }
}