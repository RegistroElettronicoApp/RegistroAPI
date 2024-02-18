package me.chicchi7393.registroapi.plugins

import freemarker.cache.ClassTemplateLoader
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.freemarker.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.dao.DAOFeedback
import me.chicchi7393.registroapi.routes.accessKeyRoute
import me.chicchi7393.registroapi.routes.fcmRoute
import me.chicchi7393.registroapi.routes.feedbackRoute

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        fcmRoute()
        accessKeyRoute()
        feedbackRoute()
        authenticate("auth-basic") {
            get("/") {
                call.respond(
                    FreeMarkerContent(
                        "index.ftl",
                        mapOf("loggedUser" to call.principal<UserIdPrincipal>()?.name)
                    )
                )
            }
            get("/feedback") {
                val daoFeedback = DAOFeedback()

                call.respond(
                    FreeMarkerContent(
                        "feedback.ftl", mapOf(
                            "loggedUser" to call.principal<UserIdPrincipal>()?.name,
                            "feedbacks" to daoFeedback.allFeedbacks()
                        )
                    )
                )
            }
            staticResources("/", "static_pages")
            staticResources("/assets", "static_pages.assets") {}
        }
    }
}
