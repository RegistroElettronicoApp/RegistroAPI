package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.dao.DAONotifications
import me.chicchi7393.registroapi.models.FCMDeleteReq
import me.chicchi7393.registroapi.models.FCMModifyReq
import me.chicchi7393.registroapi.models.NotificationEntry

fun Routing.fcmRoute() {
    route("/requestFcm") {
        val daoNotif = DAONotifications()

        put({
            description = "Request a notification token to register with the school registry"
            request {
                body<NotificationEntry> {}
            }
            response {
                HttpStatusCode.Created to {
                    description = "Created notification entry and generated server fcm"
                    body<NotificationEntry> { description = "the notification data inserted in the db" }
                }
                HttpStatusCode.InternalServerError to {
                    description = "Unable to create access key"
                }
            }
        }) {
            val notifEntry = call.receive<NotificationEntry>()
            // TODO: actually provide an fcm
            val serverFcm = java.util.UUID.randomUUID().toString()
            val result = daoNotif.addNewNotif(
                notifEntry.deviceFcm,
                notifEntry.username,
                serverFcm,
                notifEntry.reg
            )
            if (result == null) call.respondText(
                "Unable to create key",
                status = HttpStatusCode.InternalServerError
            ) else {
                call.respond(HttpStatusCode.Created, result)
            }
        }
        patch({
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