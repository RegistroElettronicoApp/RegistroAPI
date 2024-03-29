package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.models.FeedbackEntry
import me.chicchi7393.registroapi.models.FeedbackEntryTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class DAOFeedback(private val db: DatabaseClass) {
    private fun resultRowToFeedback(row: ResultRow) = FeedbackEntry(
        id = row[FeedbackEntryTable.id],
        deviceFcm = row[FeedbackEntryTable.deviceFcm],
        secret = row[FeedbackEntryTable.secret],
        name = row[FeedbackEntryTable.name],
        description = row[FeedbackEntryTable.description],
        reply = row[FeedbackEntryTable.reply],
        date = row[FeedbackEntryTable.date].atZone(ZoneId.systemDefault()).toEpochSecond()
    )

    suspend fun allFeedbacks() = db.dbQuery {
        FeedbackEntryTable.selectAll().map(::resultRowToFeedback)
    }

    suspend fun feedback(secret: String) = db.dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.secret eq secret }
            .map(::resultRowToFeedback)
            .singleOrNull()
    }

    suspend fun feedbacks(secrets: List<String>) = db.dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.secret inList secrets }
            .map(::resultRowToFeedback)
    }

    suspend fun feedbackById(id: Int) = db.dbQuery {
        FeedbackEntryTable
            .select { FeedbackEntryTable.id eq id }
            .map(::resultRowToFeedback)
            .singleOrNull()
    }

    suspend fun addFeedback(
        deviceFcm: String,
        name: String,
        description: String
    ) = db.dbQuery {
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
    ) = db.dbQuery {
        FeedbackEntryTable.update({ FeedbackEntryTable.id eq id }) {
            it[FeedbackEntryTable.reply] = reply
        } != 0
    }

    suspend fun deleteFeedback(secret: String) = db.dbQuery {
        FeedbackEntryTable.deleteWhere { FeedbackEntryTable.secret eq secret } > 0
    }

    suspend fun deleteAllFeedback() = db.dbQuery {
        FeedbackEntryTable.deleteAll()
    }
}