package me.chicchi7393.registroapi

import kotlinx.coroutines.Dispatchers
import me.chicchi7393.registroapi.models.AccessKeyTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseSingleton {
    fun init() {
        val database = Database.connect(
            url = "jdbc:h2:file:./build/db;DB_CLOSE_DELAY=-1;AUTO_SERVER=TRUE",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
        transaction(database) {
            SchemaUtils.create(AccessKeyTable)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}