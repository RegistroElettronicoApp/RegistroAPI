package me.chicchi7393.registroapi

import com.google.firebase.FirebaseApp
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.jetty.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import me.chicchi7393.registroapi.plugins.*
import org.eclipse.jetty.util.ssl.SslContextFactory

object Application {
    const val DEVELOPMENT_SERVER = true
    val dotenv: Dotenv = dotenv()
    val client = HttpClient(Jetty) {
        engine {
            sslContextFactory = SslContextFactory.Client()
            clientCacheSize = 12
        }
    }
    private val embeddedServer =
        embeddedServer(Netty, port = if (DEVELOPMENT_SERVER) 4473 else 4474, host = "0.0.0.0", module = {
            configureSecurity()
            configureHTTP()
            configureMonitoring()
            configureSerialization()
            DatabaseSingleton.init()
            configureRouting()
        }, watchPaths = listOf(
            "classes",
            "resources",
            "resources/templates"
        )
        )
    @JvmStatic
    fun main(args: Array<String>) {
        FirebaseApp.initializeApp()
        embeddedServer.start(true)
    }
}