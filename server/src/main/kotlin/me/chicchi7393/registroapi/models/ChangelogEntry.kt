package me.chicchi7393.registroapi.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class ChangelogEntry(
    val id: Int? = null,
    val versionName: String,
    val buildNumber: Int,
    val changelogHtml: String,
    val availableForUpdate: Boolean
)

object ChangelogEntryTable : Table() {
    val id = integer("id").autoIncrement()
    val versionName = varchar("versionName", 64)
    val buildNumber = integer("devicefcm")
    val changelogHtml = varchar("changelogHtml", 8096)
    val availableForUpdate = bool("availableForUpdate")
    override val primaryKey = PrimaryKey(id)
}
