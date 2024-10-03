package com.example.glamora.util

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return Regex(emailRegex).matches(email)
}

fun isPasswordEqualRePassword(pass: String , repass: String):Boolean{
    return pass == repass
}

fun isNotShort(string: String , len: Int = 7) : Boolean{
    return string.length >= len
}

fun getFirstAndLastName(fullName: String): Pair<String, String> {
    val names = fullName.split(" ")

    val firstName = names.getOrNull(0) ?: ""
    val lastName = names.getOrNull(1) ?: ""

    return Pair(firstName, lastName)
}