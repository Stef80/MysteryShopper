package com.example.misteryshopper.repository

import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.User

object UserRepository {
    private val dbHelper: DBHelper = FirebaseDBHelper.instance

    suspend fun updateUserToken(user: User, token: String) {
        user.token = token
        dbHelper.updateUsers(user, user.id!!)
    }
}
