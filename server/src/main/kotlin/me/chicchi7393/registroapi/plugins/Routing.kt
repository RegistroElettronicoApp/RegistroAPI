package me.chicchi7393.registroapi.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.doublereceive.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.dao.DAOKeyFacadeImpl
import me.chicchi7393.registroapi.models.AccessKey

fun Application.configureRouting() {
    install(AutoHeadResponse)
    install(DoubleReceive)
    routing {
        route("/requestFcm") {
            post {}
        }
        route("/accessKey") {
            val daoKey = DAOKeyFacadeImpl()
            get {
                val shareCode = call.request.queryParameters["shareCode"]
                if (shareCode == null) call.respondText(
                    "Share code not found",
                    status = HttpStatusCode.BadRequest
                ) else {
                    val keyValue = daoKey.key(shareCode)
                    if (keyValue == null) call.respondText(
                        "Not Found",
                        status = HttpStatusCode.NotFound
                    ) else {
                        call.respond(HttpStatusCode.Found, keyValue)
                    }
                }
            }
            post {
                val shareKey = call.receive<AccessKey>()
                val result = daoKey.addNewKey(
                    shareKey.schoolCode,
                    shareKey.username,
                    shareKey.password,
                    shareKey.reg,
                    shareKey.shareCode
                )
                if (result == null) call.respondText(
                    "Unable to create key",
                    status = HttpStatusCode.InternalServerError
                ) else {
                    call.respond(HttpStatusCode.Created, result)
                }
            }
        }
    }
}
