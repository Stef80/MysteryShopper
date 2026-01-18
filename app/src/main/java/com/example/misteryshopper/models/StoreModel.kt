package com.example.misteryshopper.models

import java.io.Serializable

data class StoreModel(
    @JvmField var idStore: String? = null,
    @JvmField var idEmployer: String? = null,
    @JvmField var eName: String? = null,
    @JvmField var manager: String? = null,
    @JvmField var city: String? = null,
    @JvmField var address: String? = null,
    @JvmField var imageUri: String? = null
) : Serializable
