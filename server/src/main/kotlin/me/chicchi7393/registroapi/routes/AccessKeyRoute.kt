package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.dao.DAOKey
import me.chicchi7393.registroapi.models.AccessKey

fun Routing.accessKeyRoute(db: DatabaseClass) {
    route("/accessKey") {
        val daoKey = DAOKey(db)
        get({
            tags = listOf("accessKey", "public")
            description = "Get an access key's creds"
            request {
                queryParameter<String>("shareCode") {
                    description = "your share code"
                }
            }
            response {
                HttpStatusCode.OK to {
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
                    call.respond(HttpStatusCode.OK, keyValue)
                }
            }
        }
        authenticate("auth-basic") {
            post({
                tags = listOf("accessKey", "private")
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
                    shareKey.shareCode,
                    shareKey.displayName
                )
                if (result == null) call.respondText(
                    "Unable to create key",
                    status = HttpStatusCode.InternalServerError
                ) else {
                    call.respond(HttpStatusCode.Created, result)
                }
            }
            patch({
                tags = listOf("accessKey", "private")
                description = "Edits an access key"
                request {
                    body<AccessKey> {
                        description = "the access key you have to edit"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Edited the access key"
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Unable to edit access key"
                    }
                }
            }) {
                val shareKey = call.receive<AccessKey>()
                val result = daoKey.editKey(
                    shareKey.id ?: 0,
                    shareKey.schoolCode,
                    shareKey.username,
                    shareKey.password,
                    shareKey.reg,
                    shareKey.shareCode,
                    shareKey.displayName
                )
                if (!result) call.respondText(
                    "Unable to modify key",
                    status = HttpStatusCode.InternalServerError
                ) else {
                    call.respond(HttpStatusCode.OK)
                }
            }
            delete({
                tags = listOf("accessKey", "private")
                description = "Deletes an access key"
                request {
                    queryParameter<Int>("id") {
                        description = "the access key you have to create"
                    }
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Deleted the access key"
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Unable to delete access key"
                    }
                }
            }) {
                val id = call.request.queryParameters["id"]?.toInt()
                val result = daoKey.deleteKey(id ?: -1)
                if (!result) call.respondText(
                    "Unable to create key",
                    status = HttpStatusCode.InternalServerError
                ) else {
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}