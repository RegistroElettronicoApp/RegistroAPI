package me.chicchi7393.registroapi.models.notifications

import kotlinx.serialization.Serializable

@Serializable
data class FCMTokenResponse(
    val fcm: String,
    val pk: Int
)
