package me.chicchi7393.registroapi.plugins

import freemarker.cache.ClassTemplateLoader
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.server.freemarker.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.routes.*

fun Application.configureRouting(db: DatabaseClass, dev: Boolean, client: HttpClient) {
    install(AutoHeadResponse)
    install(DoubleReceive)
    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this::class.java.classLoader, "templates")
    }
    routing {
        fcmRoute(db, client)
        accessKeyRoute(db)
        feedbackRoute(db, dev)
        administrationPageRoute(db, dev)
        debugRoute()
    }
}
