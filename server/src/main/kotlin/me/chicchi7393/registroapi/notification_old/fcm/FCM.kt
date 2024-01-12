package me.chicchi7393.registroapi.notification_old.fcm

const val FCM_SUBSCRIBE = "https://fcm.googleapis.com/fcm/connect/subscribe"
const val FCM_ENDPOINT = "https://fcm.googleapis.com/fcm/send"
/*
class FCM {
    suspend fun registerFcm(senderId: Int, token: String): RegisterResponse {
        val keys = createKeys()
        val client = HttpClient(Curl)
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
}*/