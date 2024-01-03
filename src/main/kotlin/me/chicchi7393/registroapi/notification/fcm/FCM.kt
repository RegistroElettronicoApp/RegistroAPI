package me.chicchi7393.registroapi.notification.fcm

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.json.JsonElement
import me.chicchi7393.registroapi.notification.fcm.types.Keys
import me.chicchi7393.registroapi.notification.fcm.types.RegisterResponse
import java.security.KeyPairGenerator
import java.security.spec.ECGenParameterSpec
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.random.Random

const val FCM_SUBSCRIBE = "https://fcm.googleapis.com/fcm/connect/subscribe"
const val FCM_ENDPOINT = "https://fcm.googleapis.com/fcm/send"

class FCM {
    suspend fun registerFcm(senderId: Int, token: String): RegisterResponse {
        val keys = createKeys()
        val client = HttpClient(CIO)
        val response = client.get {
            url(FCM_SUBSCRIBE)
            method = HttpMethod.Post
            formData(
                FormPart("authorized_entity", senderId),
                FormPart("endpoint", "$FCM_ENDPOINT/$token"),
                FormPart(
                    "encryption_key", keys.publicKey
                        .replace("/=/g".toRegex(), "")
                        .replace("/\\+/g".toRegex(), "-")
                        .replace("/\\//g".toRegex(), "_")
                ),
                FormPart(
                    "encryption_auth", keys.authSecret
                        .replace("/=/g".toRegex(), "")
                        .replace("/\\+/g".toRegex(), "-")
                        .replace("/\\//g".toRegex(), "_")
                )
            )
        }
        val body: JsonElement = response.body()
        return RegisterResponse(keys, "")
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun createKeys(): Keys {
        val keyPairGenerator = KeyPairGenerator.getInstance("EC", "SunEC")
        // secp256r1 [NIST P-256, X9.62 prime256v1]
        val ecParameterSpec = ECGenParameterSpec("secp256r1")
        keyPairGenerator.initialize(ecParameterSpec)
        val ecdhKeyPair = keyPairGenerator.genKeyPair()
        val privateKey = ecdhKeyPair.private
        val publicKey = ecdhKeyPair.public
        return Keys(
            Base64.encode(privateKey.encoded),
            Base64.encode(publicKey.encoded),
            Base64.encode(Random.nextBytes(16))
        )
    }
}