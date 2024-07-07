package me.chicchi7393.registroapi.models.notifications

import kotlinx.serialization.Serializable

@Serializable
data class FCMRequest(
    val regId: Int,
    val phoneFcm: String,
    val username: String
)
