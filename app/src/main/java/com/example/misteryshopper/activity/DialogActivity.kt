package com.example.misteryshopper.activity

import android.app.Activity
import android.os.Bundle
import com.example.misteryshopper.utils.DialogUIHelper

class DialogActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val notificationIntent = getIntent()
        val extras = notificationIntent.getExtras()
        val name = extras!!.getString("name")
        val outcome = extras.getString("outcome")
        DialogUIHelper.buildDialogResponse(this@DialogActivity, name!!, outcome!!)
    }
}
