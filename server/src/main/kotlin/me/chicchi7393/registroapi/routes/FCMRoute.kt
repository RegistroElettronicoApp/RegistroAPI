package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.dao.DAONotifications
import me.chicchi7393.registroapi.models.FCMAddReq
import me.chicchi7393.registroapi.models.FCMDeleteReq
import me.chicchi7393.registroapi.models.FCMModifyReq
import me.chicchi7393.registroapi.models.FCMToRegisterRes
import me.chicchi7393.registroapi.models.notifications.FCMRequest
import me.chicchi7393.registroapi.models.notifications.FCMTokenResponse

fun Route.fcmRoute(db: DatabaseClass, client: HttpClient) {
    route("/requestFcm") {
        val daoNotif = DAONotifications(db)

        put({
            tags = listOf("notifications", "public")
            description = "Request a notification token to register with the school registry"
            request {
                body<FCMAddReq> {}
            }
            response {
                HttpStatusCode.Created to {
                    description = "Created notification entry and generated server fcm"
                    body<FCMToRegisterRes> { description = "the notification data inserted in the db" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Unable to create access key"
                }
            }
        }) {
            val reqBody = call.receive<FCMAddReq>()

            val fcmRes: FCMTokenResponse = client.post("http://server-noti:3737/getfcm") {
                contentType(ContentType.Application.Json)
                setBody(FCMRequest(reqBody.reg, reqBody.deviceFcm, reqBody.username))
            }.body()
            val result = daoNotif.addNewNotif(
                reqBody.deviceFcm,
                reqBody.username,
                fcmRes.fcm,
                reqBody.reg
            )
            if (result == null) call.respondText(
                "Unable to create key",
                status = HttpStatusCode.InternalServerError
            ) else {
                call.respond(HttpStatusCode.Created, FCMToRegisterRes(fcmRes.fcm))
            }
        }
        patch({
            tags = listOf("notifications", "public")
            description = "Modifies a notification entry to change an fcm token"
            request {
                body<FCMModifyReq> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Modified successfully"
                }
                HttpStatusCode.InternalServerError to {
                    description = "Unable to modify"
                }
            }
        }) {
            val fcmEditReq = call.receive<FCMModifyReq>()
            val result = daoNotif.editFcmNotif(
                fcmEditReq.oldDeviceFcm,
                fcmEditReq.newDeviceFcm,
                fcmEditReq.username,
                fcmEditReq.reg
            )
            call.respond(HttpStatusCode.OK, result)
        }
        delete({
            tags = listOf("notifications", "public")
            description = "deletes a notification entry"
            request {
                body<FCMDeleteReq> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Modified successfully"
                }
                HttpStatusCode.InternalServerError to {
                    description = "Unable to modify"
                }
            }
        }) {
            val fcmDelReq = call.receive<FCMDeleteReq>()
            val result = daoNotif.deleteNotif(
                fcmDelReq.deviceFcm,
                fcmDelReq.username
            )
            call.respond(HttpStatusCode.OK, result)
        }
    }
}