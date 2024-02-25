package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class AccessKey(
    val id: Int? = null,
    val displayName: String,
    val schoolCode: String,
    val username: String,
    val password: String,
    val reg: Int,
    val shareCode: String
)

object AccessKeyTable : Table() {
    val id = integer("id").autoIncrement()
    val displayName = varchar("displayName", 128)
    val schoolcode = varchar("schoolcode", 128)
    val username = varchar("username", 128)
    val password = varchar("password", 128)
    val reg = integer("reg")
    val shareCode = varchar("code", 128)
    override val primaryKey = PrimaryKey(id)
}

