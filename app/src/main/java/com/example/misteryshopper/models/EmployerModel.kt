package com.example.misteryshopper.models

data class EmployerModel(
    @JvmField var emName: String? = null,
    @JvmField var category: String? = null,
    @JvmField var pIva: String? = null
) : User()
