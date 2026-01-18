package com.example.misteryshopper.models

import java.io.Serializable

data class ShopperModel(
    @JvmField var name: String? = null,
    @JvmField var surname: String? = null,
    @JvmField var address: String? = null,
    @JvmField var city: String? = null,
    @JvmField var cf: String? = null,
    @JvmField var imageUri: String? = null,
    @JvmField var totalAmount: Double = 0.0,
    @JvmField var available: Boolean = true
) : User(), Serializable
