package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class AccessKey(
    val id: Int? = null,
    val schoolCode: String,
    val username: String,
    val password: String,
    val reg: String,
    val shareCode: String
)

object AccessKeyTable : Table() {
    val id = integer("id").autoIncrement()
    val schoolcode = varchar("schoolcode", 128)
    val username = varchar("username", 128)
    val password = varchar("password", 128)
    val reg = varchar("reg", 32)
    val shareCode = varchar("code", 128)
    override val primaryKey = PrimaryKey(id)
}

