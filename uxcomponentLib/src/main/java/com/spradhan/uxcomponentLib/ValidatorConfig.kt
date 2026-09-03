package com.spradhan.uxcomponentLib

data class ValidatorConfig(
    val validator: (String) -> Boolean,
    val errorMessage: String
)