package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

@Serializable
data class FeedbackEntry(
    val id: Int? = null,
    val deviceFcm: String = "",
    val secret: String = "",
    val name: String = "",
    val description: String = "",
    val reply: String = "",
    val date: Long = 0L
)

object FeedbackEntryTable : Table() {
    val id = integer("id").autoIncrement()
    val secret = varchar("secret", 64)
    val deviceFcm = varchar("devicefcm", 128)
    val name = varchar("name", 128)
    val description = varchar("description", 2000)
    val reply = varchar("reply", 2000)
    val date = datetime("date").defaultExpression(CurrentDateTime)
    override val primaryKey = PrimaryKey(id)
}
