package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class FeedbackReplyPayload(
    val id: Int,
    val reply: String
)