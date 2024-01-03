package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    data class Session(val count: Int = 0)
    install(Sessions) {
        cookie<Session>("KTOR_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
}
