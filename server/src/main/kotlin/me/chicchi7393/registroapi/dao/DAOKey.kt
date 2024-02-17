package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.DatabaseSingleton.dbQuery
import me.chicchi7393.registroapi.models.AccessKey
import me.chicchi7393.registroapi.models.AccessKeyTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class DAOKey {
    private fun resultRowToKey(row: ResultRow) = AccessKey(
        id = row[AccessKeyTable.id],
        schoolCode = row[AccessKeyTable.schoolcode],
        username = row[AccessKeyTable.username],
        password = row[AccessKeyTable.password],
        reg = row[AccessKeyTable.reg],
        shareCode = row[AccessKeyTable.shareCode],
    )

    suspend fun allKeys() = dbQuery {
        AccessKeyTable.selectAll().map(::resultRowToKey)
    }

    suspend fun key(shareCode: String) = dbQuery {
        AccessKeyTable
            .select { AccessKeyTable.shareCode eq shareCode }
            .map(::resultRowToKey)
            .singleOrNull()
    }

    suspend fun addNewKey(
        schoolCode: String,
        username: String,
        password: String,
        reg: Int,
        shareCode: String
    ) = dbQuery {
        val insertStatement = AccessKeyTable.insert {
            it[schoolcode] = schoolCode
            it[AccessKeyTable.username] = username
            it[AccessKeyTable.password] = password
            it[AccessKeyTable.reg] = reg
            it[AccessKeyTable.shareCode] = shareCode
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToKey)
    }

    suspend fun editArticle(
        id: Int,
        schoolCode: String,
        username: String,
        password: String,
        reg: Int,
        shareCode: String
    ) = dbQuery {
        AccessKeyTable.update({ AccessKeyTable.id eq id }) {
            it[schoolcode] = schoolcode
            it[AccessKeyTable.username] = username
            it[AccessKeyTable.password] = password
            it[AccessKeyTable.reg] = reg
            it[AccessKeyTable.shareCode] = shareCode
        } != 0
    }

    suspend fun deleteArticle(shareCode: String) = dbQuery {
        AccessKeyTable.deleteWhere { AccessKeyTable.shareCode eq shareCode } > 0
    }
}