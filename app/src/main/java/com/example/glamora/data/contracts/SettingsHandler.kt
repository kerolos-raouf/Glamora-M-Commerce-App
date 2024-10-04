package com.example.glamora.data.contracts

interface SettingsHandler {

    fun setSharedPrefString(key: String, value: String)
    fun getSharedPrefString(key: String, defaultValue : String) : String

    fun setSharedPrefBoolean(key: String, value: Boolean)
    fun getSharedPrefBoolean(key: String, defaultValue : Boolean) : Boolean

}