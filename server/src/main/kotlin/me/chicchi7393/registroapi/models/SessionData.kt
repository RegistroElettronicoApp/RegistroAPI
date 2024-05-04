package me.chicchi7393.registroapi.models

import io.ktor.server.auth.*

data class SessionData(
    val name: String = "",
    val password: String = "",
    val isLogged: Boolean = false
) : Principal
