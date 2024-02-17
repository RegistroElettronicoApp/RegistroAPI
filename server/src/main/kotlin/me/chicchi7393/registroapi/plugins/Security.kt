package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*

fun Application.configureSecurity() {
    data class Session(val count: Int = 0)
    install(Sessions) {
        cookie<Session>("KTOR_SESSION") {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    install(Authentication) {
        basic("auth-basic") {
            realm = "Access to the '/' path"
            validate { credentials ->
                if (credentials.password == me.chicchi7393.registroapi.Application.dotenv["PANEL_KEY"]) {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
