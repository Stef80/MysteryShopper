package com.example.misteryshopper.models

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

data class HiringModel(
    @JvmField var id: String? = null,
    @JvmField var idEmployer: String? = null,
    @JvmField var employerName: String? = null,
    @JvmField var address: String? = null,
    @JvmField var mailShopper: String? = null,
    @JvmField var idStore: String? = null,
    @JvmField var date: String? = null,
    @JvmField var fee: Double = 0.0,
    @JvmField var accepted: String? = null,
    @JvmField var done: Boolean = false
) : Comparable<HiringModel> {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun compareTo(other: HiringModel): Int {
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        var date1: Date? = null
        var date2: Date? = null
        try {
            date2 = format.parse(other.date)
            date1 = format.parse(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        if (date1 == null || date2 == null) {
            return 0
        }
        return date1.compareTo(date2)
    }
}
