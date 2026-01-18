package com.example.misteryshopper.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.example.misteryshopper.MainActivity
import com.example.misteryshopper.R
import com.example.misteryshopper.activity.RegisterEmployerActivity
import com.example.misteryshopper.activity.RegisterShopperActivity
import com.example.misteryshopper.activity.StoreListActivity
import com.example.misteryshopper.datbase.DBHelper
import com.example.misteryshopper.datbase.impl.FirebaseDBHelper
import com.example.misteryshopper.models.EmployerModel
import com.example.misteryshopper.models.HiringModel
import com.example.misteryshopper.models.StoreModel
import com.example.misteryshopper.utils.camera.PictureHandler
import com.example.misteryshopper.utils.notification.MessageCreationService

object DialogUIHelper {

    private lateinit var sharedPref: SharedPrefConfig

    fun createRegistationDialog(context: Context) {
        sharedPref = SharedPrefConfig(context)
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_main, null)
        val dialog = builder.create()
        dialog.setView(dialogView)
        val retryBtn = dialogView.findViewById<Button>(R.id.retry_button)
        retryBtn.setOnClickListener { dialog.dismiss() }
        val regShopBtn = dialogView.findViewById<Button>(R.id.reg_shop_button)
        regShopBtn.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    RegisterShopperActivity::class.java
                )
            )
        }
        val regEmpBtn = dialogView.findViewById<Button>(R.id.reg_empl_button)
        regEmpBtn.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    RegisterEmployerActivity::class.java
                )
            )
        }
        dialog.show()
    }

    fun createStoreDialog(model: StoreModel, context: Context) {
        sharedPref = SharedPrefConfig(context)
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_add_store, null)
        val dialog = builder.create()
        dialog.setView(dialogView)
        val idShop = dialogView.findViewById<EditText>(R.id.id_store_add)
        val manager = dialogView.findViewById<EditText>(R.id.manager_name_store_add)
        val city = dialogView.findViewById<EditText>(R.id.city_store_add)
        val address = dialogView.findViewById<EditText>(R.id.address_store_add)
        val addImage = dialogView.findViewById<Button>(R.id.image_add_dialog)
        val btnAdd = dialogView.findViewById<Button>(R.id.button_store_add)
        val btnCancel = dialogView.findViewById<Button>(R.id.button_store_cancel)
        addImage.setOnClickListener {
            val storeId = idShop.text.toString()
            if (TextUtils.isEmpty(storeId)) idShop.error =
                context.getString(R.string.field_required) else {
                sharedPref.writePrefString("store_id", storeId)
                val activity = context as StoreListActivity
                PictureHandler.getPicture(activity)
            }
        }
        btnCancel.setOnClickListener { onClick -> dialog.dismiss() }
        btnAdd.setOnClickListener {
            val id = idShop.text.toString()
            val managerStr = manager.text.toString()
            val cityStr = city.text.toString()
            val adr = address.text.toString()
            if (TextUtils.isEmpty(id)) idShop.error = context.getString(R.string.field_required)
            if (TextUtils.isEmpty(managerStr)) manager.error =
                context.getString(R.string.field_required)
            if (TextUtils.isEmpty(cityStr)) city.error =
                context.getString(R.string.field_required)
            if (TextUtils.isEmpty(adr)) address.error =
                context.getString(R.string.field_required)
            val user = sharedPref.readLoggedUser() as EmployerModel
            model.idStore = id
            model.eName = user.emName
            model.manager = manager.text.toString()
            model.city = city.text.toString()
            model.address = address.text.toString()
            model.imageUri = SharedPrefConfig(context).readPrefString("imageUri")
            Log.i("USERINDIALOG", user.toString())
            model.idEmployer = user.id
            if (!(TextUtils.isEmpty(id) && TextUtils.isEmpty(managerStr) && TextUtils.isEmpty(
                    cityStr
                ) && TextUtils.isEmpty(adr))
            ) {
                mDBHelper.addStoreOfSpecificId(model, object : DBHelper.DataStatus {
                    override fun dataIsLoaded(obj: List<*>?, keys: List<String>?) {
                        if (obj != null) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.shop_added),
                                Toast.LENGTH_LONG
                            ).show()
                            dialog.dismiss()
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.shop_not_added),
                                Toast.LENGTH_LONG
                            ).show()
                            idShop.setText("")
                            manager.setText("")
                            city.setText("")
                            address.setText("")
                        }
                    }
                })
            }
        }
        dialog.show()
    }

    fun buildDialogHire(
        context: Context,
        model: StoreModel,
        idEmployer: String,
        mailShopper: String
    ) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_hire, null)
        val dialog = builder.create()
        dialog.setView(dialogView)
        val date = dialogView.findViewById<DatePicker>(R.id.date_picker_ref)
        val storeId = dialogView.findViewById<TextView>(R.id.store_id_ref)
        val shopperMail = dialogView.findViewById<TextView>(R.id.mail_shopper_ref)
        val address = dialogView.findViewById<TextView>(R.id.address_store_ref)
        val fee = dialogView.findViewById<EditText>(R.id.fee_edtxt_ref)
        val buttonHire = dialogView.findViewById<Button>(R.id.button_hire)
        dialogView.findViewById<View>(R.id.button_cancel_ref)
            .setOnClickListener { x -> dialog.dismiss() }
        storeId.text = model.idStore
        address.text = model.city + "\n" + model.address
        shopperMail.text = mailShopper
        date.minDate = System.currentTimeMillis()
        buttonHire.setOnClickListener {
            val dateStr = date.dayOfMonth.toString() + "/" +
                    (date.month + 1) + "/" +
                    date.year
            val feeStr = fee.text.toString()
            if (TextUtils.isEmpty(feeStr)) {
                fee.error = context.getString(R.string.field_required)
            } else {
                val feeNumber = feeStr.toDouble()
                val now = System.currentTimeMillis().toString()
                val addressStore = address.text.toString()
                val hiringModel = HiringModel(
                    now + idEmployer, idEmployer,
                    model.eName, mailShopper, addressStore, model.idStore, dateStr, feeNumber
                )
                mDBHelper.addHiringModel(hiringModel, object : DBHelper.DataStatus {
                    override fun dataIsLoaded(obj: List<*>?, keys: List<String>?) {
                        mDBHelper.getTokenByMail(
                            mailShopper
                        ) { obj1, keys1 ->
                            if (obj1.isNotEmpty() && obj1[0] != null) {
                                Log.i("DILAOGTOKEN", obj1[0] as String)
                                MessageCreationService.buildMessage(
                                    context, obj1[0] as String,
                                    context.getString(R.string.notification_of_employment),
                                    model.city + "\n" + model.address,
                                    dateStr,
                                    feeNumber.toString(),
                                    model.eName,
                                    idEmployer,
                                    hiringModel.id,
                                    model.imageUri
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "token error plz try again",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                        dialog.dismiss()
                    }
                })
            }
        }
        dialog.show()
    }

    fun buildDialogResponse(context: Context, name: String, outcome: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val dialogView = inflater.inflate(R.layout.dialog_response, null)
        val dialog = builder.create()
        dialog.setView(dialogView)
        val nameView = dialogView.findViewById<TextView>(R.id.dialog_respnse_message_name)
        val outcomeView = dialogView.findViewById<TextView>(R.id.dialog_response_outcome)
        val btnOk = dialogView.findViewById<Button>(R.id.dialog_ok_button)
        nameView.text = name
        outcomeView.text = outcome
        btnOk.setOnClickListener { x ->
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            dialog.dismiss()
        }
        dialog.show()
    }
}
