package me.chicchi7393.registroapi

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.chicchi7393.registroapi.plugins.*

object Application {
    const val DEVELOPMENT_SERVER = true
    val dotenv: Dotenv = dotenv()
    private val embeddedServer =
        embeddedServer(Netty, port = if (DEVELOPMENT_SERVER) 4473 else 4474, host = "0.0.0.0", module = {
            configureSecurity()
            configureHTTP()
            configureMonitoring()
            configureSerialization()
            DatabaseSingleton.init()
            configureRouting()
        }
        )

    @JvmStatic
    fun main(args: Array<String>) {
        embeddedServer.start(true)
    }
}