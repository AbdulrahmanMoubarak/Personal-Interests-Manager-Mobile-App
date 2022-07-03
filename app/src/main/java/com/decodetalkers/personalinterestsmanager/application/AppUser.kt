package com.decodetalkers.personalinterestsmanager.application

object AppUser {
    var user_id = -1
    var user_name = "user"
    var user_email = "email"
    var user_image = "content://media/external/images/media/7004"

    fun setUserData(id: Int, name: String, email: String){
        user_id = id
        user_name = name
        user_email = email
    }
}