package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable

@Serializable
data class ChangelogDeletePayload(
    val id: Int
)