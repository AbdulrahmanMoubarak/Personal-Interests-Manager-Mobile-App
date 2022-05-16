package com.decodetalkers.personalinterestsmanager.ui.util

class FormValidator {

    fun validateLogin(email: String, password: String): Boolean {
        return email != "" && email.contains("@") && email.contains(".com") && password != ""
    }

    fun validateRegisteration(
        email: String,
        username: String,
        password: String,
        passwordConfirm: String
    ) : Boolean{
        if (email == "" || !email.contains("@") || !email.contains(".com"))
            return false
        if( username == "")
            return false
        if(password =="" || passwordConfirm == "" || password.length < 6 || !password.equals(passwordConfirm))
            return false
        return true
    }
}