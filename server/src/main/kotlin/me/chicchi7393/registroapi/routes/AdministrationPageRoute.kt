package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import me.chicchi7393.registroapi.Application.dotenv
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.dao.DAOFeedback
import me.chicchi7393.registroapi.dao.DAOKey
import me.chicchi7393.registroapi.models.FrontendCreds
import me.chicchi7393.registroapi.models.SessionData

fun Route.administrationPageRoute(db: DatabaseClass, dev: Boolean) {
    suspend fun homePage(call: ApplicationCall, error: Boolean, logout: Boolean) {
        val session = call.sessions.get<SessionData>()
        if (session == null) {
            call.sessions.set(SessionData())
        }
        call.respond(
            FreeMarkerContent(
                "index.ftl",
                mapOf(
                    "logged" to (session?.isLogged ?: false),
                    "name" to (session?.name ?: false),
                    "error" to error,
                    "logout" to logout,
                    "dev" to dev
                )
            )
        )
    }
    route("/") {
        get({
            tags = listOf("frontend", "public")
        }) {
            homePage(call, false, false)
        }

        authenticate("auth-form") {
            post({
                tags = listOf("frontend", "public")
            }) {
                val creds = call.principal<FrontendCreds>()
                if (creds != null && creds.password == dotenv["PANEL_KEY"]) {
                    call.sessions.set(
                        SessionData(
                            creds.name,
                            creds.password,
                            isLogged = true
                        )
                    )
                    call.respondRedirect("/")
                } else {
                    homePage(call, true, false)
                }
            }
        }
    }
    route("/logout") {
        post({
            tags = listOf("frontend", "private")
        }) {
            call.sessions.clear<SessionData>()
            homePage(call, false, true)
        }
    }
    staticResources("/assets", "static_pages.assets") {}
    authenticate("auth-session") {
        route("/feedbackManager") {
            get({
                tags = listOf("frontend", "private")
            }) {
                val session = call.principal<SessionData>()
                val daoFeedback = DAOFeedback(db)
                call.respond(
                    FreeMarkerContent(
                        "feedbackManager.ftl", mapOf(
                            "loggedUser" to call.principal<UserIdPrincipal>()?.name,
                            "feedbacks" to daoFeedback.allFeedbacks(),
                            "logged" to (session?.isLogged ?: false),
                            "name" to (session?.name ?: false),
                            "error" to false,
                            "dev" to dev
                        )
                    )
                )
            }
        }

        route("/accessKeysManager") {
            get({
                tags = listOf("frontend", "private")
            }) {
                val session = call.principal<SessionData>()
                val daoKeys = DAOKey(db)
                call.respond(
                    FreeMarkerContent(
                        "accessKeys.ftl", mapOf(
                            "loggedUser" to call.principal<UserIdPrincipal>()?.name,
                            "keys" to daoKeys.allKeys(),
                            "logged" to (session?.isLogged ?: false),
                            "dev" to dev
                        )
                    )
                )
            }
        }
        staticResources("/", "static_pages")
    }
}