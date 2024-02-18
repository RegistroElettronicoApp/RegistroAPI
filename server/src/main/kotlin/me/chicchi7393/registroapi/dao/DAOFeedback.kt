package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.DatabaseSingleton.dbQuery
import me.chicchi7393.registroapi.models.FeedbackEntry
import me.chicchi7393.registroapi.models.FeedbackEntryTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DAOFeedback {
    private fun resultRowToFeedback(row: ResultRow) = FeedbackEntry(
        id = row[FeedbackEntryTable.id],
        deviceFcm = row[FeedbackEntryTable.deviceFcm],
        secret = row[FeedbackEntryTable.secret],
        name = row[FeedbackEntryTable.name],
        description = row[FeedbackEntryTable.description],
        reply = row[FeedbackEntryTable.reply],
        date = row[FeedbackEntryTable.date].atZone(ZoneId.systemDefault()).toEpochSecond()
    )

    suspend fun allFeedbacks() = dbQuery {
        FeedbackEntryTable.selectAll().map(::resultRowToFeedback)
    }

    suspend fun feedback(secret: String) = dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.secret eq secret }
            .map(::resultRowToFeedback)
            .singleOrNull()
    }
    suspend fun feedbacks(secrets: List<String>) = dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.secret inList secrets }
            .map(::resultRowToFeedback)
    }
    suspend fun feedbackById(id: Int) = dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.id eq id }
            .map(::resultRowToFeedback)
            .singleOrNull()
    }

    suspend fun addFeedback(
        deviceFcm: String,
        name: String,
        description: String
    ) = dbQuery {
        val insertStatement = FeedbackEntryTable.insert {
            it[FeedbackEntryTable.deviceFcm] = deviceFcm
            it[secret] = UUID.randomUUID().toString()
            it[FeedbackEntryTable.name] = name
            it[FeedbackEntryTable.description] = description
            it[reply] = ""
            it[date] = LocalDateTime.now()
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToFeedback)
    }

    suspend fun replyFeedback(
        id: Int,
        reply: String
    ) = dbQuery {
        FeedbackEntryTable.update({ FeedbackEntryTable.id eq id }) {
            it[FeedbackEntryTable.reply] = reply
        } != 0
    }

    suspend fun deleteFeedback(secret: String) = dbQuery {
        FeedbackEntryTable.deleteWhere { FeedbackEntryTable.secret eq secret } > 0
    }
    suspend fun deleteAllFeedback() = dbQuery {
        FeedbackEntryTable.deleteAll()
    }
}