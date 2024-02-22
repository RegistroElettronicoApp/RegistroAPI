package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.DatabaseClass
import me.chicchi7393.registroapi.models.NotificationEntry
import me.chicchi7393.registroapi.models.NotificationEntryTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAONotifications(private val db: DatabaseClass) {
    private fun resultRowToNotif(row: ResultRow) = NotificationEntry(
        id = row[NotificationEntryTable.id],
        deviceFcm = row[NotificationEntryTable.deviceFcm],
        username = row[NotificationEntryTable.username],
        serverFcm = row[NotificationEntryTable.serverFcm],
        reg = row[NotificationEntryTable.reg]
    )

    suspend fun allNotifs() = db.dbQuery {
        NotificationEntryTable.selectAll().map(::resultRowToNotif)
    }

    suspend fun notif(deviceFcm: String, username: String) = db.dbQuery {
        NotificationEntryTable
            .select { NotificationEntryTable.username eq username and (NotificationEntryTable.deviceFcm eq deviceFcm) }
            .map(::resultRowToNotif)
            .singleOrNull()
    }

    suspend fun addNewNotif(
        deviceFcm: String,
        username: String,
        serverFcm: String,
        reg: Int
    ) = db.dbQuery {
        val insertStatement = NotificationEntryTable.insert {
            it[NotificationEntryTable.deviceFcm] = deviceFcm
            it[NotificationEntryTable.username] = username
            it[NotificationEntryTable.serverFcm] = serverFcm
            it[NotificationEntryTable.reg] = reg
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToNotif)
    }

    suspend fun editNotif(
        id: Int,
        deviceFcm: String,
        username: String,
        serverFcm: String,
        reg: Int
    ) = db.dbQuery {
        NotificationEntryTable.update({ NotificationEntryTable.id eq id }) {
            it[NotificationEntryTable.deviceFcm] = deviceFcm
            it[NotificationEntryTable.username] = username
            it[NotificationEntryTable.serverFcm] = serverFcm
            it[NotificationEntryTable.reg] = reg
        } != 0
    }

    suspend fun editFcmNotif(
        oldDeviceFcm: String,
        newDeviceFcm: String,
        username: String,
        reg: Int
    ) = db.dbQuery {
        NotificationEntryTable.update({ NotificationEntryTable.deviceFcm eq oldDeviceFcm }) {
            it[deviceFcm] = newDeviceFcm
            it[NotificationEntryTable.username] = username
            it[NotificationEntryTable.reg] = reg
        } != 0
    }

    suspend fun deleteNotif(deviceFcm: String, username: String) = db.dbQuery {
        NotificationEntryTable.deleteWhere { NotificationEntryTable.username eq username and (NotificationEntryTable.deviceFcm eq deviceFcm) } > 0
    }
}