package me.chicchi7393.registroapi.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        route("/requestFcm") {
            post {}
        }
    }
}
