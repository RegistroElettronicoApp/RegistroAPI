package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.delete
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.*
import me.chicchi7393.registroapi.Application
import me.chicchi7393.registroapi.dao.DAOFeedback
import me.chicchi7393.registroapi.models.FeedbackDeletePayload
import me.chicchi7393.registroapi.models.FeedbackEntry

fun Routing.feedbackRoute() {
    route("/feedback") {
        val daoFeedback = DAOFeedback()

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
        delete({
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
}