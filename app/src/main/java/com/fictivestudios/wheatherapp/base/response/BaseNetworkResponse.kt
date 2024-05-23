package com.fictivestudios.wheatherapp.base.response

data class BaseNetworkResponse<T>(
    val status: Int,
    val message: String,
    val token: String? = null,
    val data: T?
)