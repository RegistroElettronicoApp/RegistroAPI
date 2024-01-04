package me.chicchi7393.registroapi

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.chicchi7393.registroapi.plugins.*

object Application {
    private val embeddedServer = embeddedServer(Netty, port = 4473, host = "0.0.0.0", module = {
        configureSecurity()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
        configureDatabases()
        configureRouting()
    })

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer.start(true)
    }
}