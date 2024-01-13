package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.dao.DAOKeyFacadeImpl
import me.chicchi7393.registroapi.models.AccessKey

fun Routing.accessKeyRoute() {
    route("/accessKey") {
        val daoKey = DAOKeyFacadeImpl()
        get({
            description = "Get an access key's creds"
            request {
                pathParameter<String>("shareCode") {
                    description = "your share code"
                }
            }
            response {
                HttpStatusCode.Found to {
                    description = "Found creds"
                    body<AccessKey> { description = "the creds associated to the code" }
                }
                HttpStatusCode.NotFound to {
                    description = "No access key found"
                }
                HttpStatusCode.BadRequest to {
                    description = "Access key not provided"
                }
            }
        }) {
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
        post({
            description = "Create an access key"
            request {
                body<AccessKey> {
                    description = "the access key you have to create"
                }
            }
            response {
                HttpStatusCode.Created to {
                    description = "Created the access key"
                    body<AccessKey> { description = "the access key you just created" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Unable to create access key"
                }
            }
        }) {
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