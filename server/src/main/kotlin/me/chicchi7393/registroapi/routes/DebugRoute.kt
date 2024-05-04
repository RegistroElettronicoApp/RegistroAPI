package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.sentry.Sentry

fun Route.debugRoute() {
    route("/throwDebugError") {
        authenticate("auth-session") {
            post({
                tags = listOf("debug", "private")
                description = "Throws an error, to test Sentry"
                response {
                    HttpStatusCode.InternalServerError to {
                        description = "try to guess"
                    }
                }
            }) {
                try {
                    throw Exception("This is a test.")
                } catch (e: Exception) {
                    Sentry.captureException(e)
                }
            }
        }
    }
}