package me.chicchi7393.registroapi.routes

import io.github.smiley4.ktorswaggerui.dsl.get
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

fun Route.recapRoute(dev: Boolean) {
    route("/recap") {
        get({
            tags = listOf("recap", "public")
            description = "Fetches the recap, if existent"
            request {
                headerParameter<String>(HttpHeaders.AcceptLanguage)
            }
            response {
                HttpStatusCode.OK to {
                    description = "Recap existent"
                    body<String>({
                        description = "Corresponding lottie animation"
                    })
                }
                HttpStatusCode.NotFound to {
                    description = "No current recap found"
                    body<String>()
                }

                HttpStatusCode.InternalServerError to {
                    description = "Internal server errors"
                    body<String>()
                }
            }
        }) {
            try {
                val language = call.request.acceptLanguage()

                val files = Path("./recaps").listDirectoryEntries()
                val validRecaps = files.filter {
                    !it.fileName.name.contains(".disabled") && (if (!dev) true else it.fileName.name.contains("dev"))
                }

                if (validRecaps.isEmpty()) call.respondText(
                    "No current, valid recap found",
                    status = HttpStatusCode.NotFound
                )

                val recap = validRecaps.firstOrNull { file ->
                    language?.let {
                        file.fileName.name.contains(it)
                    } ?: false
                } ?: files.firstOrNull { file ->
                    file.fileName.name.contains("en")
                }

                if (recap == null) call.respondText(
                    "No current, valid recap found",
                    status = HttpStatusCode.NotFound
                ) else {
                    call.respondOutputStream(
                        contentType = ContentType.Application.Json,
                        status = HttpStatusCode.OK,
                        producer = {
                            Files.copy(recap, this)
                        }
                    )
                }
            } catch (e: Exception) {
                call.respond(HttpStatusCode.InternalServerError, "An error occured: ${e.message}")
            }
        }
    }
}