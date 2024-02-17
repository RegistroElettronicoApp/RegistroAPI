package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.http.content.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.routes.accessKeyRoute
import me.chicchi7393.registroapi.routes.fcmRoute
import me.chicchi7393.registroapi.routes.feedbackRoute

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        fcmRoute()
        accessKeyRoute()
        feedbackRoute()
        authenticate("auth-basic") {
            staticResources("/", "static_pages") {}
        }
    }
}
