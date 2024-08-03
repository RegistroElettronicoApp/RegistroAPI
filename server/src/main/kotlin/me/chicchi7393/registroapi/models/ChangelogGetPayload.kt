package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class ChangelogGetPayload(
    val build: Int
)