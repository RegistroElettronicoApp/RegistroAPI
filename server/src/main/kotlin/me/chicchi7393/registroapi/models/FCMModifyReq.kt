package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class FCMModifyReq(
    val oldDeviceFcm: String,
    val newDeviceFcm: String,
    val username: String,
    val reg: Int
)

