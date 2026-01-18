package com.example.misteryshopper.utils.notification

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

class MySingleton private constructor(private val ctx: Context) {
    private var requestQueue: RequestQueue?

    init {
        requestQueue = getRequestQueue()
    }

    fun getRequestQueue(): RequestQueue {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext())
        }
        return requestQueue!!
    }

    fun <T> addToRequestQueue(req: Request<T?>?) {
        getRequestQueue().add<T?>(req)
    }

    companion object {
        private var instance: MySingleton? = null

        @Synchronized
        fun getInstance(context: Context): MySingleton {
            if (instance == null) {
                instance = MySingleton(context)
            }
            return instance!!
        }
    }
}
