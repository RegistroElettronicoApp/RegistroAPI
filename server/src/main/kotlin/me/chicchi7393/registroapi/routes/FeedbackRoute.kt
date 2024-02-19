package me.chicchi7393.registroapi.routes

import com.google.firebase.messaging.*
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.chicchi7393.registroapi.Application
import me.chicchi7393.registroapi.dao.DAOFeedback
import me.chicchi7393.registroapi.models.*

fun Routing.feedbackRoute() {
    val daoFeedback = DAOFeedback()

    route("/feedback") {
        put({
            description = "Opens a feedback"
            request {
                body<FeedbackEntry> {}
            }
            response {
                HttpStatusCode.Created to {
                    description = "Feedback opened"
                    body<FeedbackEntry>()
                }
                HttpStatusCode.BadRequest to {
                    description = "Field errors"
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val feedbackEntry = call.receive<FeedbackEntry>()
                val result =
                    daoFeedback.addFeedback(feedbackEntry.deviceFcm, feedbackEntry.name, feedbackEntry.description)
                if (result == null) call.respondText(
                    "Unable to create feedback",
                    status = HttpStatusCode.BadRequest
                ) else {
                    val response = Application.client.submitForm(
                        "https://api.telegram.org/bot${Application.dotenv["TG_BOT"]}/sendMessage",
                        parameters {
                            append("chat_id", Application.dotenv["TG_GROUP"])
                            append(
                                "text", """
                                <b>Nuovo feedback!</b>
                                <b>Da:</b> ${feedbackEntry.name.escapeHTML()}
                                <b>Messaggio:</b> ${feedbackEntry.description.escapeHTML()}
                                <b>ID:</b> ${result.id}
                                
                                https://regapi${if (Application.DEVELOPMENT_SERVER) "-dev" else ""}.chicchi7393.xyz
                            """.trimIndent()
                            )
                            append("parse_mode", "HTML")
                        }
                    )
                    println(response.status)
                    println(response.bodyAsText())
                    call.respond(HttpStatusCode.Created, result)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }
        authenticate("auth-basic") {
            patch({
                description = "Replies to a feedback"
                request {
                    body<FeedbackReplyPayload> {}
                }
                response {
                    HttpStatusCode.OK to {
                        description = "Feedback replied"
                    }
                    HttpStatusCode.NotFound to {
                        description = "Feedback with given id not found"
                        body<String>()
                    }
                    HttpStatusCode.InternalServerError to {
                        description = "Internal server errors"
                        body<String>()
                    }
                }
            }) {
                try {
                    val feedbackReplyEntry = call.receive<FeedbackReplyPayload>()
                    val result = daoFeedback.replyFeedback(feedbackReplyEntry.id, feedbackReplyEntry.reply)
                    val feedbackEntry = daoFeedback.feedbackById(feedbackReplyEntry.id)
                    if (!result && feedbackEntry == null) call.respondText(
                        "No feedback found",
                        status = HttpStatusCode.NotFound
                    ) else {
                        FirebaseMessaging.getInstance().send(
                            Message.builder()
                                .setToken(feedbackEntry?.deviceFcm)
                                .setNotification(
                                    Notification.builder()
                                        .setTitle("Nuova risposta al tuo feedback")
                                        .setBody("Il tuo feedback ha ricevuto una risposta")
                                        .build()
                                )
                                .setAndroidConfig(
                                    AndroidConfig.builder()
                                        .putData("feedbackSecret", feedbackEntry?.secret)
                                        .setNotification(
                                            AndroidNotification.builder()
                                                .setTitle("Nuova risposta al tuo feedback")
                                                .setBody("Il tuo feedback ha ricevuto una risposta")
                                                .build()
                                        )
                                        .setPriority(AndroidConfig.Priority.HIGH)
                                        .build()
                                )
                                .build()
                        )
                        call.respond(HttpStatusCode.OK)
                    }
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
                }
            }
        }
    }
    route("/deleteFeedback") {
        post({
            description = "Deletes a feedback"
            request {
                body<FeedbackDeletePayload> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Feedback deleted"
                }
                HttpStatusCode.NotFound to {
                    description = "Feedback with given secret not found"
                    body<String>()
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val feedbackDeleteEntry = call.receive<FeedbackDeletePayload>()
                val result = daoFeedback.deleteFeedback(feedbackDeleteEntry.secret)
                if (!result) call.respondText(
                    "No feedback found",
                    status = HttpStatusCode.NotFound
                ) else {
                    call.respond(HttpStatusCode.OK)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }
    }
    route("/getFeedback") {
        post({
            description = "Gets a feedback"
            request {
                body<FeedbackGetPayload> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Feedback found"
                    body<FeedbackEntry>()
                }
                HttpStatusCode.NotFound to {
                    description = "Feedback with given secret not found"
                    body<String>()
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val feedbackGetEntry = call.receive<FeedbackGetPayload>()
                val result = daoFeedback.feedback(feedbackGetEntry.secret)
                if (result == null) call.respondText(
                    "No feedback found",
                    status = HttpStatusCode.NotFound
                ) else {
                    call.respond(HttpStatusCode.OK, result)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }
    }
    route("/getFeedbacks") {
        post({
            description = "Gets a list of feedbacks"
            request {
                body<FeedbackGetListPayload> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Feedback found"
                    body<List<FeedbackEntry>>()
                }
                HttpStatusCode.NotFound to {
                    description = "Feedback with given secret not found"
                    body<List<FeedbackEntry>>()
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val feedbackGetEntry = call.receive<FeedbackGetListPayload>()
                val result = daoFeedback.feedbacks(feedbackGetEntry.secrets)
                if (result.isEmpty()) call.respond(
                    HttpStatusCode.NotFound,
                    listOf<FeedbackEntry>()
                ) else {
                    call.respond(HttpStatusCode.OK, result)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }

    }
    route("deleteAllFeedback") {
        authenticate("auth-basic") {
            delete({
                description = "Deletes all feedback"
                request {}
                response {
                    HttpStatusCode.OK to {
                        description = "All Feedback deleted"
                    }
                }
            }) {
                daoFeedback.deleteAllFeedback()
                call.respond(HttpStatusCode.OK)
            }
        }
    }
}