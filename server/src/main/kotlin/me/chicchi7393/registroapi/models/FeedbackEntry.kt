package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class FeedbackEntry(
    val id: Int? = null,
    val deviceFcm: String = "",
    val secret: String = "",
    val name: String = "",
    val description: String = "",
    val reply: String = ""
)

object FeedbackEntryTable : Table() {
    val id = integer("id").autoIncrement()
    val secret = varchar("secret", 64)
    val deviceFcm = varchar("devicefcm", 128)
    val name = varchar("name", 128)
    val description = varchar("description", 2000)
    val reply = varchar("reply", 2000)
    override val primaryKey = PrimaryKey(id)
}
