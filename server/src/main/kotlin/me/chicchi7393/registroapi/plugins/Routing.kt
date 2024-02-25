package me.chicchi7393.registroapi.plugins

import freemarker.cache.ClassTemplateLoader
import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.dao.DAOFeedback
import me.chicchi7393.registroapi.dao.DAOKey
import me.chicchi7393.registroapi.routes.accessKeyRoute
import me.chicchi7393.registroapi.routes.debugRoute
import me.chicchi7393.registroapi.routes.fcmRoute
import me.chicchi7393.registroapi.routes.feedbackRoute

fun Application.configureRouting(db: DatabaseClass, dev: Boolean) {
    install(AutoHeadResponse)
    install(DoubleReceive)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        fcmRoute(db)
        accessKeyRoute(db)
        feedbackRoute(db, dev)
        debugRoute()
        authenticate("auth-basic") {
            route("/") {
                get({
                    tags = listOf("frontend", "private")
                }) {
                    call.respond(
                        FreeMarkerContent(
                            "index.ftl",
                            mapOf("loggedUser" to call.principal<UserIdPrincipal>()?.name)
                        )
                    )
                }
            }
            route("/feedbackManager") {
                get({
                    tags = listOf("frontend", "private")
                }) {
                    val daoFeedback = DAOFeedback(db)
                    call.respond(
                        FreeMarkerContent(
                            "feedbackManager.ftl", mapOf(
                                "loggedUser" to call.principal<UserIdPrincipal>()?.name,
                                "feedbacks" to daoFeedback.allFeedbacks()
                            )
                        )
                    )
                }
            }

            route("/accessKeysManager") {
                get({
                    tags = listOf("frontend", "private")
                }) {
                    val daoKeys = DAOKey(db)
                    call.respond(
                        FreeMarkerContent(
                            "accessKeys.ftl", mapOf(
                                "loggedUser" to call.principal<UserIdPrincipal>()?.name,
                                "keys" to daoKeys.allKeys()
                            )
                        )
                    )
                }
            }
            staticResources("/", "static_pages")
            staticResources("/assets", "static_pages.assets") {}
        }
    }
}
