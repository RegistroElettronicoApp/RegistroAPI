package me.chicchi7393.registroapi

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.engine.java.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.sentry.Sentry
import me.chicchi7393.registroapi.plugins.*
import java.io.IOException
import java.io.InputStream

object Application {
    val dotenv: Dotenv = dotenv()
    val client = HttpClient(Java) {
        engine {
            pipelining = true
            protocolVersion = java.net.http.HttpClient.Version.HTTP_2
        }
        install(ContentNegotiation) {
            json()
        }
    }

    private val embeddedServerDev = let {
        try {
            println("killing previous dev server")
            ProcessBuilder("fuser", "4473/tcp", "-k").start().waitFor()
        } catch (e: IOException) {
            println("unable to kill previous server, maybe fuser not found?")
        }
        embeddedServer(
            Netty, port = 4473, host = "0.0.0.0", module = {
                configureSecurity()
                configureHTTP(true)
                configureMonitoring()
                configureSerialization()
                val devDb = DatabaseClass(true)
                configureRouting(devDb, true, client)
            }, watchPaths = listOf(
                "classes",
                "resources",
                "resources/templates"
            )
        ).apply {
            println("started dev server")
        }
    }

    private val embeddedServerProd = let {
        try {
            println("killing previous prod server")
            ProcessBuilder("fuser", "4474/tcp", "-k").start().waitFor()
        } catch (e: IOException) {
            println("unable to kill previous server, maybe fuser not found?")
        }
        embeddedServer(
            Netty, port = 4474, host = "0.0.0.0", module = {
                configureSecurity()
                configureHTTP(false)
                configureMonitoring()
                configureSerialization()
                val prodDb = DatabaseClass(false)
                configureRouting(prodDb, false, client)
            }, watchPaths = listOf(
                "classes",
                "resources",
                "resources/templates"
            )
        ).apply {
            println("started prod server")
        }
    }

    @JvmStatic
    fun main(args: Array<String>) {
        FirebaseApp.initializeApp(
            FirebaseOptions.builder()
                .setCredentials(
                    GoogleCredentials.fromStream(
                        this::class.java.getResource("/firebase-service-acc.json")?.openStream()
                            ?: InputStream.nullInputStream()
                    )
                )
                .build()
        )
        Sentry.init { options ->
            options.dsn = "https://e43059ddae1cc7ed0f0e68139068aa49@sentry.chicchi7393.xyz/3"
            // Set tracesSampleRate to 1.0 to capture 100% of transactions for performance monitoring.
            // We recommend adjusting this value in production.
            options.tracesSampleRate = 1.0
            // When first trying Sentry it's good to see what the SDK is doing:
            options.isDebug = true
        }
        embeddedServerDev.start(false)
        embeddedServerProd.start(true)
    }
}