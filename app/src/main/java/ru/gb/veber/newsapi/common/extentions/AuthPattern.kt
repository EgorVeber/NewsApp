package ru.gb.veber.newsapi.common.extentions

import java.util.regex.Pattern

object AuthPattern {
    const val LOGIN_EXAMPLE: String = "UserName"
    const val EMAIL_EXAMPLE: String = "User1@gmail.com"
    const val PASSWORD_EXAMPLE: String = "Example Zydfhm2022?"
    val EMAIL_PATTERN: Pattern = Pattern.compile("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    val PASSWORD_PATTERN: Pattern = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^\\w\\s]).{6,20}")
    val LOGIN_PATTERN: Pattern = Pattern.compile("^[A-Z](?=[a-zA-Z0-9._]{4,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")
}