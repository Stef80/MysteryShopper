package com.example.misteryshopper.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.misteryshopper.models.EmployerModel
import com.example.misteryshopper.models.ShopperModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.models.User
import com.google.gson.Gson

class SharedPrefConfig(context: Context) {
    private val DATA = "user_data"
    private val USER_VALUE = "user"
    private val preferences: SharedPreferences
    private val context: Context?
    private val g = Gson()

    init {
        this.context = context
        preferences = context.getSharedPreferences(DATA, Context.MODE_PRIVATE)
    }

    fun writeLoggedUser(user: User?) {
        val editor = preferences.edit()
        editor.putString(USER_VALUE, getJSON(user))
        editor.commit()
    }

    fun readLoggedUser(): User? {
        var userVal = ""
        userVal = preferences.getString(USER_VALUE, "")!!
        return formJSON(userVal) as User?
    }

    fun writePrefString(key: String?, data: String?) {
        val editor = preferences.edit()
        editor.putString(key, data)
        editor.commit()
    }

    fun readPrefString(key: String?): String {
        return preferences.getString(key, "")!!
    }

    private fun getJSON(usr: Any?): String {
        return g.toJson(usr)
    }

    fun formJSON(json: String): Any? {
        if (json.contains(SHOPPER)) {
            val model = g.fromJson<ShopperModel?>(json, ShopperModel::class.java)
            return model
        } else if (json.contains(EMPLOYER)) {
            val model = g.fromJson<EmployerModel?>(json, EmployerModel::class.java)
            return model
        } else {
            return null
        }
    }

    private fun fromJSONToStore(json: String?): StoreModel? {
        val storeModel = g.fromJson<StoreModel?>(json, StoreModel::class.java)
        return storeModel
    }

    fun cancelData() {
        val editor = preferences.edit()
        editor.clear()
        editor.commit()
    }

    fun writPrefStore(store: StoreModel?) {
        preferences.edit().putString(STORE, getJSON(store)).commit()
    }

    fun readPrefStore(): StoreModel? {
        val storeJSON: String = preferences.getString(STORE, "")!!
        return fromJSONToStore(storeJSON)
    }

    companion object {
        private const val SHOPPER = "Shopper"
        private const val EMPLOYER = "Employer"
        private const val STORE = "store"
    }
}
