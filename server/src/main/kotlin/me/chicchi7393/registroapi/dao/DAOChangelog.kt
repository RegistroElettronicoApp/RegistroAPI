package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.models.ChangelogEntry
import me.chicchi7393.registroapi.models.ChangelogEntryTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOChangelog(private val db: DatabaseClass) {
    private fun resultRowToChangelog(row: ResultRow) = ChangelogEntry(
        id = row[ChangelogEntryTable.id],
        versionName = row[ChangelogEntryTable.versionName],
        buildNumber = row[ChangelogEntryTable.buildNumber],
        changelogHtml = row[ChangelogEntryTable.changelogHtml],
        availableForUpdate = row[ChangelogEntryTable.availableForUpdate]
    )

    suspend fun allChangelogs() = db.dbQuery {
        ChangelogEntryTable.selectAll().map(::resultRowToChangelog)
    }

    suspend fun changelog(build: Int) = db.dbQuery {
        ChangelogEntryTable
            .select { ChangelogEntryTable.buildNumber eq build }
            .map(::resultRowToChangelog)
            .singleOrNull()
    }

    suspend fun changelogs(builds: List<Int>) = db.dbQuery {
        ChangelogEntryTable
            .select { ChangelogEntryTable.buildNumber inList builds }
            .map(::resultRowToChangelog)
    }

    suspend fun changelogById(id: Int) = db.dbQuery {
        ChangelogEntryTable
            .select { ChangelogEntryTable.id eq id }
            .map(::resultRowToChangelog)
            .singleOrNull()
    }

    suspend fun addChangelog(
        versionName: String,
        buildNumber: Int,
        changelog: String,
        availableForUpdate: Boolean
    ) = db.dbQuery {
        val insertStatement = ChangelogEntryTable.insert {
            it[ChangelogEntryTable.versionName] = versionName
            it[ChangelogEntryTable.buildNumber] = buildNumber
            it[changelogHtml] = changelog
            it[ChangelogEntryTable.availableForUpdate] = availableForUpdate
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToChangelog)
    }

    suspend fun editChangelog(
        id: Int,
        versionName: String,
        buildNumber: Int,
        changelog: String,
        availableForUpdate: Boolean
    ) = db.dbQuery {
        ChangelogEntryTable.update({ ChangelogEntryTable.id eq id }) {
            it[ChangelogEntryTable.versionName] = versionName
            it[ChangelogEntryTable.buildNumber] = buildNumber
            it[changelogHtml] = changelog
            it[ChangelogEntryTable.availableForUpdate] = availableForUpdate
        } != 0
    }

    suspend fun deleteChangelog(id: Int) = db.dbQuery {
        ChangelogEntryTable.deleteWhere { ChangelogEntryTable.id eq id } > 0
    }

    suspend fun deleteAllChangelogs() = db.dbQuery {
        ChangelogEntryTable.deleteAll()
    }
}