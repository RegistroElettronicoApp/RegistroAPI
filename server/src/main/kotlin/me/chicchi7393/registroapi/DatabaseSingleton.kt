package me.chicchi7393.registroapi

import kotlinx.coroutines.Dispatchers
import me.chicchi7393.registroapi.models.AccessKeyTable
import me.chicchi7393.registroapi.models.FeedbackEntryTable
import me.chicchi7393.registroapi.models.NotificationEntryTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseClass(dev: Boolean) {
    init {
        val database = Database.connect(
            url = "jdbc:mongodb://127.0.0.1/registroapi",
            driver = "mongodb.jdbc.MongoDriver"
        )
        transaction(database) {
            SchemaUtils.create(AccessKeyTable)
            SchemaUtils.create(NotificationEntryTable)
            SchemaUtils.create(FeedbackEntryTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}