package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class NotificationEntry(
    val id: Int? = null,
    val deviceFcm: String,
    val username: String,
    val serverFcm: String,
    val reg: Int
)

object NotificationEntryTable : Table() {
    val id = integer("id").autoIncrement()
    val deviceFcm = varchar("devicefcm", 128)
    val username = varchar("username", 128)
    val serverFcm = varchar("serverfcm", 128)
    val reg = integer("reg")
    override val primaryKey = PrimaryKey(id)
}
