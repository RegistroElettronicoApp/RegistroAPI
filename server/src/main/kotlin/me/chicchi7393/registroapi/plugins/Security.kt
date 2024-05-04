package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.sessions.*
import io.ktor.util.*
import me.chicchi7393.registroapi.Application.dotenv
import me.chicchi7393.registroapi.models.FrontendCreds
import me.chicchi7393.registroapi.models.SessionData

fun Application.configureSecurity() {
    install(Sessions) {
        val secretEncryptKey = hex(dotenv["ENC_KEY"])
        val secretSignKey = hex(dotenv["SIGN_KEY"])
        cookie<SessionData>("KTOR_SESSION") {
            cookie.extensions["SameSite"] = "lax"
            cookie.maxAgeInSeconds = 86400
            transform(SessionTransportTransformerEncrypt(secretEncryptKey, secretSignKey))
        }
    }
    install(Authentication) {
        form("auth-form") {
            userParamName = "username"
            passwordParamName = "password"
            validate { credentials ->
                if (credentials.password == dotenv["PANEL_KEY"]) {
                    FrontendCreds(credentials.name, credentials.password)
                } else {
                    null
                }
            }
        }
        session<SessionData>("session") {
            validate {
                it
            }
        }

        session<SessionData>("auth-session") {
            validate { session ->
                if (session.isLogged) {
                    session
                } else {
                    null
                }
            }
            challenge {
                call.respondRedirect("/#loginNec")
            }
        }
    }
}
