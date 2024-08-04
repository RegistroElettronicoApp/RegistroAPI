package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.patch
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.dao.DAOChangelog
import me.chicchi7393.registroapi.models.ChangelogDeletePayload
import me.chicchi7393.registroapi.models.ChangelogEntry
import me.chicchi7393.registroapi.models.ChangelogGetPayload
import me.chicchi7393.registroapi.models.FeedbackEntry

fun Route.changelogRoute(db: DatabaseClass, dev: Boolean) {
    val daoChangelog = DAOChangelog(db)

        authenticate("auth-session") {
            route("/changelog") {

                put({
                    tags = listOf("changelog", "private")
                    description = "Inserts a changelog"
                    request {
                        body<ChangelogEntry> {}
                    }
                    response {
                        HttpStatusCode.Created to {
                            description = "Changelog inserted"
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
                        val changelogEntry = call.receive<ChangelogEntry>()
                        val result =
                            daoChangelog.addChangelog(
                                changelogEntry.versionName,
                                changelogEntry.buildNumber,
                                changelogEntry.changelogHtml,
                                changelogEntry.availableForUpdate
                            )
                        if (result == null) call.respondText(
                            "Unable to create changelog",
                            status = HttpStatusCode.BadRequest
                        ) else {
                            call.respond(HttpStatusCode.Created, result)
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
                    }
                }

                patch({
                    tags = listOf("feedback", "private")
                    description = "Modifies a changelog"
                    request {
                        body<ChangelogEntry> {}
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Changelog modified"
                        }
                        HttpStatusCode.NotFound to {
                            description = "Changelog with given id not found"
                            body<String>()
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal server errors"
                            body<String>()
                        }
                    }
                }) {
                    try {
                        val changelogEntry = call.receive<ChangelogEntry>()
                        if (changelogEntry.id == null) call.respondText(
                            "No feedback found",
                            status = HttpStatusCode.NotFound
                        )
                        val result = daoChangelog.editChangelog(
                            changelogEntry.id!!,
                            changelogEntry.versionName,
                            changelogEntry.buildNumber,
                            changelogEntry.changelogHtml,
                            changelogEntry.availableForUpdate
                        )
                        if (!result) call.respondText(
                            "No changelog found",
                            status = HttpStatusCode.NotFound
                        ) else {
                            call.respond(HttpStatusCode.OK)
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
                    }
                }
            }
            route("/deleteChangelog") {
                post({
                    tags = listOf("changelog", "private")
                    description = "Deletes a changelog"
                    request {
                        body<ChangelogDeletePayload> {}
                    }
                    response {
                        HttpStatusCode.OK to {
                            description = "Changelog deleted"
                        }
                        HttpStatusCode.NotFound to {
                            description = "Changelog with given id not found"
                            body<String>()
                        }
                        HttpStatusCode.InternalServerError to {
                            description = "Internal server errors"
                            body<String>()
                        }
                    }
                }) {
                    try {
                        val changelogDeleteEntry = call.receive<ChangelogDeletePayload>()
                        val result = daoChangelog.deleteChangelog(changelogDeleteEntry.id)
                        if (!result) call.respondText(
                            "No changelog found",
                            status = HttpStatusCode.NotFound
                        ) else {
                            call.respondText(
                                "",
                                status = HttpStatusCode.OK
                            )
                        }
                    } catch (e: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
                    }
                }
            }
        }


    route("/getChangelog") {
        post({
            tags = listOf("changelog", "public")
            description = "Gets a changelog"
            request {
                body<ChangelogGetPayload> {}
            }
            response {
                HttpStatusCode.OK to {
                    description = "Changelog found"
                    body<ChangelogEntry>()
                }
                HttpStatusCode.NotFound to {
                    description = "Changelog with given build not found"
                    body<String>()
                }
                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val changelogGetEntry = call.receive<ChangelogGetPayload>()
                val result = daoChangelog.changelog(changelogGetEntry.build)
                if (result == null) call.respondText(
                    "No changelog found",
                    status = HttpStatusCode.NotFound
                ) else {
                    call.respond(HttpStatusCode.OK, result)
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }
    }
}