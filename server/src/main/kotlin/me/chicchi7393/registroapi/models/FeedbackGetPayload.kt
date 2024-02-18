package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackGetPayload(
    val secret: String
)