package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.routes.accessKeyRoute

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        route("/requestFcm") {
            post {}
        }
        accessKeyRoute()
    }
}
