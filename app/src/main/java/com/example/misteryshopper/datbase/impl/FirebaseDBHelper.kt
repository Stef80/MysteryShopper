package com.example.misteryshopper.datbase.impl

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FirebaseDBHelper private constructor() : DBHelper {
    private val USER = "User"
    private val EMPLOYER = "Employer"
    private val SHOPPER = "Shopper"

    private val mDatabase: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val mStorage: FirebaseStorage = FirebaseStorage.getInstance()

    override suspend fun readShoppers(): List<ShopperModel> {
        val query = mDatabase.getReference(USER).orderByChild("role").equalTo(SHOPPER)
        return doQuery(query, ShopperModel::class.java)
    }

    override suspend fun register(model: User, email: String, password: String, context: Context): String? {
        val result = mAuth.createUserWithEmailAndPassword(email, password).await()
        val uId = result.user?.uid
        if (uId != null) {
            model.id = uId
            when (model) {
                is EmployerModel -> model.role = EMPLOYER
                is ShopperModel -> model.role = SHOPPER
            }
            updateUsers(model, uId, context)
        }
        return uId
    }

    override suspend fun login(userMail: String, password: String, context: Context): User? {
        val result = mAuth.signInWithEmailAndPassword(userMail, password).await()
        val uId = result.user?.uid ?: return null
        val role = getRole(uId)
        return if (role == SHOPPER) {
            getShopperByMail(userMail)
        } else {
            getEmployerByMail(userMail)
        }
    }

    override fun signOut(context: Context) {
        mAuth.signOut()
    }

    override suspend fun getShopperByMail(mail: String): ShopperModel? {
        val query = mDatabase.getReference(USER).orderByChild("email").equalTo(mail)
        return doQuery<ShopperModel>(query, ShopperModel::class.java).firstOrNull()
    }

    override suspend fun getEmployerByMail(mail: String): EmployerModel? {
        val query = mDatabase.getReference(USER).orderByChild("email").equalTo(mail)
        return doQuery<EmployerModel>(query, EmployerModel::class.java).firstOrNull()
    }

    override suspend fun getUserById(uId: String): User? {
        val query = mDatabase.getReference(USER).orderByChild("id").equalTo(uId)
        return doQuery<User>(query, User::class.java).firstOrNull()
    }

    override suspend fun getRole(uId: String): String? {
        val query = mDatabase.getReference(USER).child(uId).child("role")
        return suspendCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    continuation.resume(snapshot.getValue(String::class.java))
                }
                override fun onCancelled(error: DatabaseError) {
                    continuation.resumeWithException(error.toException())
                }
            })
        }
    }

    override suspend fun readStoreOfSpecificUser(uId: String): List<StoreModel> {
        val query = mDatabase.getReference(STORE).orderByChild("idEmployer").equalTo(uId)
        return doQuery(query, StoreModel::class.java)
    }

    override suspend fun addStoreOfSpecificId(model: StoreModel) {
        if (!model.idStore.isNullOrEmpty()) {
            mDatabase.getReference(STORE).child(model.idStore!!).setValue(model).await()
        }
    }

    override suspend fun addTokenToUser(user: User, context: Context): String? {
        val token = FirebaseMessaging.getInstance().token.await()
        if (user.id != null) {
            mDatabase.getReference(USER).child(user.id!!).child("token").setValue(token).await()
        }
        return token
    }

    override suspend fun getTokenByMail(mail: String): String? {
         val query = mDatabase.getReference(USER).orderByChild("email").equalTo(mail)
        val user = doQuery<User>(query, User::class.java).firstOrNull()
        return user?.token
    }

    override suspend fun getTokenById(id: String): String? {
        val query = mDatabase.getReference(USER).orderByChild("id").equalTo(id)
        val user = doQuery<User>(query, User::class.java).firstOrNull()
        return user?.token
    }

    override suspend fun addHiringModel(model: HiringModel) {
        if (!model.id.isNullOrEmpty()) {
            mDatabase.getReference(HIRE).child(model.id!!).setValue(model).await()
        }
    }

    override suspend fun setOutcome(hId: String, outcome: String?) {
        mDatabase.getReference(HIRE).child(hId).child("accepted").setValue(outcome).await()
    }

    override suspend fun getHireByMail(mail: String): List<HiringModel> {
        val query = mDatabase.getReference(HIRE).orderByChild("mailShopper").equalTo(mail)
        return doQuery(query, HiringModel::class.java)
    }

    override suspend fun setHireDone(id: String) {
        mDatabase.getReference(HIRE).child(id).child("done").setValue(true).await()
    }

    override suspend fun addImageToUserById(id: String, imageUri: Uri, context: Context) {
        val downloadUrl = uploadImage(imageUri, context)
        mDatabase.getReference(USER).child(id).child("imageUri").setValue(downloadUrl.toString()).await()
    }

    override suspend fun addImageToStoreById(id: String, imageUri: Uri, context: Context) {
        val downloadUrl = uploadImage(imageUri, context)
        mDatabase.getReference(STORE).child(id).child("imageUri").setValue(downloadUrl.toString()).await()
    }

    private suspend fun uploadImage(imageUri: Uri, context: Context): Uri {
        val fileReference = mStorage.reference.child("uploads")
            .child("${System.currentTimeMillis()}.${getFileExtension(imageUri, context)}")
        val uploadTask = fileReference.putFile(imageUri).await()
        return uploadTask.storage.downloadUrl.await()
    }

    override suspend fun setTotalForUserId(id: String, totalAmount: Double) {
        mDatabase.getReference(USER).child(id).child("totalAmount").setValue(totalAmount).await()
    }

    override val idCurrentUser: String?
        get() = mAuth.currentUser?.uid

    suspend fun updateUsers(model: User?, uId: String, context: Context?) {
        mDatabase.getReference(USER).child(uId).setValue(model).await()
    }

    private suspend fun <T> doQuery(query: Query, myClass: Class<T>): List<T> {
        return suspendCoroutine { continuation ->
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val list = mutableListOf<T>()
                    for (node in dataSnapshot.children) {
                        node.getValue(myClass)?.let { list.add(it) }
                    }
                    continuation.resume(list)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    continuation.resumeWithException(databaseError.toException())
                }
            })
        }
    }

    private fun getFileExtension(uri: Uri, context: Context): String? {
        val cR: ContentResolver = context.contentResolver
        val mime: MimeTypeMap = MimeTypeMap.getSingleton()
        return mime.getExtensionFromMimeType(cR.getType(uri))
    }

    companion object {
        private const val STORE = "Store"
        private const val HIRE = "Hire"
        private var mDbHelper: DBHelper? = null

        @JvmStatic
        val instance: DBHelper
            get() {
                if (mDbHelper == null) {
                    mDbHelper = FirebaseDBHelper()
                }
                return mDbHelper!!
            }
    }
}
