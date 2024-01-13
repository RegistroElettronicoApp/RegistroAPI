package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class FCMDeleteReq(
    val deviceFcm: String,
    val username: String,
    val reg: Int
)

