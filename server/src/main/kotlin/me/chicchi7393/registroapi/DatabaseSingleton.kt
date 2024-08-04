package me.chicchi7393.registroapi

import kotlinx.coroutines.Dispatchers
import me.chicchi7393.registroapi.models.AccessKeyTable
import me.chicchi7393.registroapi.models.ChangelogEntryTable
import me.chicchi7393.registroapi.models.FeedbackEntryTable
import me.chicchi7393.registroapi.models.NotificationEntryTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseClass(dev: Boolean) {
    init {
        val database = Database.connect(
            url = "jdbc:postgresql://db:5432/registroapi${if (dev) "_dev" else ""}",
            user = "postgres",
            password = "iserniaesplosa"
        )
        transaction(database) {
            SchemaUtils.create(AccessKeyTable)
            SchemaUtils.create(NotificationEntryTable)
            SchemaUtils.create(FeedbackEntryTable)
            SchemaUtils.create(ChangelogEntryTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}