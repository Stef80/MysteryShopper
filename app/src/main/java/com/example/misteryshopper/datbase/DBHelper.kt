package com.example.misteryshopper.datbase

import android.content.Context
import android.net.Uri
import com.example.misteryshopper.models.*

interface DBHelper {

    suspend fun readShoppers(): List<ShopperModel>
    suspend fun register(model: User, email: String, password: String, context: Context): String?
    suspend fun login(userMail: String, password: String, context: Context): User?
    fun signOut(context: Context)
    suspend fun getShopperByMail(mail: String): ShopperModel?
    suspend fun getEmployerByMail(mail: String): EmployerModel?
    suspend fun getUserById(uId: String): User?
    suspend fun getRole(uId: String): String?
    suspend fun readStoreOfSpecificUser(uId: String): List<StoreModel>
    suspend fun addStoreOfSpecificId(model: StoreModel)
    suspend fun addTokenToUser(user: User, context: Context): String?
    suspend fun getTokenByMail(mail: String): String?
    suspend fun getTokenById(id: String): String?
    suspend fun addHiringModel(model: HiringModel)
    suspend fun setOutcome(hId: String, outcome: String?)
    suspend fun getHireByMail(mail: String): List<HiringModel>
    suspend fun setHireDone(id: String)
    suspend fun addImageToUserById(id: String, imageUri: Uri, context: Context)
    suspend fun addImageToStoreById(id: String, imageUri: Uri, context: Context)
    suspend fun setTotalForUserId(id: String, totalAmount: Double)
    suspend fun updateUsers(model: User?, uId: String)
    val idCurrentUser: String?

    // The DataStatus interface is no longer needed with coroutines
    interface DataStatus {
        fun dataIsLoaded(obj: MutableList<*>?, keys: MutableList<String?>?)
    }
}
