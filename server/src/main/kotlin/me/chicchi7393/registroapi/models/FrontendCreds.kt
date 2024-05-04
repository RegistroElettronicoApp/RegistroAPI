package me.chicchi7393.registroapi.models

import io.ktor.server.auth.*

data class FrontendCreds(
    val name: String = "",
    val password: String = "",
) : Principal
