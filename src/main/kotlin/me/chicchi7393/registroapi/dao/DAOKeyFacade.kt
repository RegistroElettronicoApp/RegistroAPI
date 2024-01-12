package me.chicchi7393.registroapi.dao

import me.chicchi7393.registroapi.models.AccessKey

interface DAOKeyFacade {
    suspend fun allKeys(): List<AccessKey>
    suspend fun key(shareCode: String): AccessKey?
    suspend fun addNewKey(
        schoolCode: String,
        username: String,
        password: String,
        reg: String,
        shareCode: String
    ): AccessKey?

    suspend fun editArticle(
        id: Int,
        schoolCode: String,
        username: String,
        password: String,
        reg: String,
        shareCode: String
    ): Boolean

    suspend fun deleteArticle(shareCode: String): Boolean
}