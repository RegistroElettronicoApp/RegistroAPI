package me.chicchi7393.registroapi.notifications

import checkin_proto.AndroidCheckin
import checkin_proto.AndroidCheckin.ChromeBuildProto
import checkin_proto.Checkin.AndroidCheckinRequest
import checkin_proto.Checkin.AndroidCheckinResponse
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.util.*
import io.ktor.utils.io.*
import me.chicchi7393.registroapi.Application
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object PushReciever {
    val SERVER_KEY = listOf(
        0x04,
        0x33,
        0x94,
        0xf7,
        0xdf,
        0xa1,
        0xeb,
        0xb1,
        0xdc,
        0x03,
        0xa2,
        0x5e,
        0x15,
        0x71,
        0xdb,
        0x48,
        0xd3,
        0x2e,
        0xed,
        0xed,
        0xb2,
        0x34,
        0xdb,
        0xb7,
        0x47,
        0x3a,
        0x0c,
        0x8f,
        0xc4,
        0xcc,
        0xe1,
        0x6f,
        0x3c,
        0x8c,
        0x84,
        0xdf,
        0xab,
        0xb6,
        0x66,
        0x3e,
        0xf2,
        0x0c,
        0xd4,
        0x8b,
        0xfe,
        0xe3,
        0xf9,
        0x76,
        0x2f,
        0x14,
        0x1c,
        0x63,
        0x08,
        0x6a,
        0x6f,
        0x2d,
        0xb1,
        0x1a,
        0x95,
        0xb0,
        0xce,
        0x37,
        0xc0,
        0x9c,
        0x6e
    ).map {
        it.toByte()
    }.toByteArray()

    const val REGISTER_URL = "https://android.clients.google.com/c2dm/register3"
    const val CHECKIN_URL = "https://android.clients.google.com/checkin"
    const val FCM_SUBSCRIBE = "https://fcm.googleapis.com/fcm/connect/subscribe"
    const val FCM_ENDPOINT = "https://fcm.googleapis.com/fcm/send"

    suspend fun _do_request(httpRequest: HttpRequestBuilder): ByteReadChannel {
        return Application.client.request(httpRequest).body()
    }

    /*
    * perform check-in request
    * androidId, securityToken can be provided if we already did the initial check-in
    * returns dict with androidId, securityToken and more
    */
    suspend fun gcmCheckIn(_androidId: Long? = null, _securityToken: Long? = null): AndroidCheckinResponse {
        val chrome = ChromeBuildProto.newBuilder()
            .setPlatform(ChromeBuildProto.Platform.PLATFORM_LINUX)
            .setChromeVersion("63.0.3234.0")
            .setChannel(ChromeBuildProto.Channel.CHANNEL_STABLE)
            .build()
        val checkin = AndroidCheckin.AndroidCheckinProto.newBuilder()
            .setType(AndroidCheckin.DeviceType.DEVICE_CHROME_BROWSER)
            .setChromeBuild(chrome)
            .build()
        val payload = AndroidCheckinRequest.newBuilder()
            .setUserSerialNumber(0)
            .setCheckin(checkin)
            .setVersion(3)
            .apply {
                if (_androidId != null) id = _androidId
                if (_securityToken != null) securityToken = _securityToken
            }
            .build()

        println(payload)

        val respData = _do_request(HttpRequestBuilder.invoke(CHECKIN_URL).apply {
            header("Content-Type", "application/x-protobuf")
            setBody(payload.toString())
        })
        val resp = AndroidCheckinResponse.parseFrom(respData.toByteArray())
        println(resp)
        return resp
    }

    /**
     * base64-encodes data with -_ instead of +/ and removes all = padding.
     *   also strips newlines
     *
     *   returns a string
     */
    @OptIn(ExperimentalEncodingApi::class)
    fun urlSafeBase64(data: ByteArray): String {
        val res = Base64.encode(data).replace("+", "-").replace("/", "_").replace("=", "")
        return res.replace("\n", "")
    }

    /**
     * obtains a gcm token
     *
     *   appId: app id as an integer
     *   retries: number of failed requests before giving up
     *
     *   returns {"token": "...", "appId": 123123, "androidId":123123,
     *            "securityToken": 123123}
     */
    suspend fun gcmRegister(appId: String, retries: Int = 5): Map<String, String>? {
        // contains androidId, securityToken and more
        val chk = gcmCheckIn()
        val body = FormDataContent(Parameters.build {
            append("app", "org.chromium.linux")
            append("X-subtype", appId)
            append("device", chk.androidId.toString())
            append("sender", urlSafeBase64(SERVER_KEY))
        })
        mapOf(
            "app" to "org.chromium.linux",
            "X-subtype" to appId,
            "device" to chk.androidId,
            "sender" to urlSafeBase64(SERVER_KEY)
        )
        val auth = "AidLogin ${chk.androidId}:${chk.securityToken}"
        val req = HttpRequestBuilder.invoke(REGISTER_URL).apply {
            header("Authorization", auth)
            setBody(body)
        }
        (1..retries).forEach {
            val respData = _do_request(req)
            if (respData.toByteArray().toString().contains("Error")) {
                val err = respData.toByteArray().toString()
                println("Register request has failed with $err")
            } else {
                val token = respData.toByteArray().toString().split("=")[1]
                val res = mapOf(
                    "token" to token, "appId" to appId,
                    "androidId" to chk.androidId.toString(),
                    "securityToken" to chk.securityToken.toString()
                )
                return res
            }
        }
        return null
    }
}