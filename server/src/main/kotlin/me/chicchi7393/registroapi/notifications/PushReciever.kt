package me.chicchi7393.registroapi.notifications


/*object PushReciever {
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
    const val MTALK_HOST = "mtalk.google.com"

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

        LoggingHelper.log(LoggingHelper.LogType.DEBUG, payload.toString())

        val respData = _do_request(HttpRequestBuilder.invoke(CHECKIN_URL).apply {
            header("Content-Type", "application/x-protobuf")
            setBody(payload.toString())
        })
        val resp = AndroidCheckinResponse.parseFrom(respData.toByteArray())
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, resp.toString())
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
                LoggingHelper.log(LoggingHelper.LogType.ERROR, "Register request has failed with $err")
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

    /**
     * generates key pair and obtains a fcm token
     *
     *   sender_id: sender id as an integer
     *   token: the subscription token in the dict returned by gcm_register
     *
     *   returns {"keys": keys, "fcm": {...}}
     */
    @OptIn(ExperimentalEncodingApi::class)
    suspend fun fcmRegister(senderId: Int, token: String, retries: Int = 5): Map<String, Map<String, String>> {
        val ecSpec = ECNamedCurveTable.getParameterSpec("prime256v1")
        val generator = KeyPairGenerator.getInstance("ECDSA", "SC")
        generator.initialize(ecSpec, SecureRandom())
        val keyPair = generator.generateKeyPair()
        val pubKey = keyPair.public
        val pvtKey = keyPair.private

        val pubAsn1Key = getASN1Dump(pubKey.encoded)
        val privAsn1Key = getASN1Dump(pvtKey.encoded)

        LoggingHelper.log(LoggingHelper.LogType.DEBUG, "PUBKEY: ${Base64.encode(pubAsn1Key.toByteArray())}")
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, "PRIVKEY: ${Base64.encode(privAsn1Key.toByteArray())}")

        val keys = mapOf(
            "public" to urlSafeBase64(pubAsn1Key.substring(26).toByteArray()),
            "private" to urlSafeBase64(privAsn1Key.toByteArray()),
            "secret" to urlSafeBase64(urandom(16))
        )

        val data = mapOf(
            "authorized_entity" to senderId,
            "endpoint" to "{}/{}".format(FCM_ENDPOINT, token),
            "encryption_key" to keys["public"],
            "encryption_auth" to keys["secret"]
        )

        LoggingHelper.log(LoggingHelper.LogType.DEBUG, data.toString())

        val resp_data = _do_request(HttpRequestBuilder.invoke(FCM_SUBSCRIBE).apply {
            setBody(data)
        })
        return mapOf("keys" to keys, "fcm" to Json.decodeFromString(resp_data.readUTF8Line() ?: ""))
    }

    /**
     * register gcm and fcm tokens for sender_id
     */
    suspend fun register(senderId: Int): MutableMap<String, Map<String, String>?> {
        val appId = "wp:receiver.push.com#${UUID.randomUUID()}"
        val subscription = gcmRegister(appId=appId)
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, subscription.toString())
        val fcm = fcmRegister(senderId, subscription?.get("token") ?: "")
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, fcm.toString())
        val res = mutableMapOf("gcm" to subscription)
        res += fcm
        return res
    }
    /* --------------------------------------------------------------------------------- */
    val MCS_VERSION = 41

    val PACKET_BY_TAG = listOf<Any>(
        Msc.HeartbeatPing::class.java,
        Msc.HeartbeatAck::class.java,
        Msc.LoginRequest::class.java,
        Msc.LoginResponse::class.java,
        Msc.Close::class.java,
        "MessageStanza",
        "PresenceStanza",
        Msc.IqStanza::class.java,
        Msc.DataMessageStanza::class.java,
        "BatchPresenceStanza",
        Msc.StreamErrorStanza::class.java,
        "HttpRequest",
        "HttpResponse",
        "BindAccountRequest",
        "BindAccountResponse",
        "TalkMetadata"
    )
    fun __send(s: Socket, packet: Any) {
        val header = byteArrayOf(MCS_VERSION.toByte(), PACKET_BY_TAG.indexOf(packet::class.java).toByte())
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, packet.toString())
        val payload = packet.toString()
        val buf = header + __encode_varint32(payload.length) + payload
        __log.debug(hexlify(buf))
        n = len(buf)
        total = 0
        while total < n:
        sent = s.send(buf[total:])
        if sent == 0:
        raise RuntimeError("socket connection broken")
        total += sent
    }

    suspend fun __listen(s: Socket, credentials: Map<String, Map<String, String>>, callback: (Map<String, String>, Map<String, String>, Map<String, String>) -> Unit, persistent_ids: List<String>) {
        gcmCheckIn(credentials["gcm"]?.get("androidId")?.toLong(), credentials["gcm"]?.get("securityToken")?.toLong())
        val req = Msc.LoginRequest.newBuilder()
            .setAdaptiveHeartbeat(false)
            .setAuthService(Msc.LoginRequest.AuthService.ANDROID_ID)
            .setAuthToken(credentials["gcm"]?.get("securityToken") ?: "")
            .setId("chrome-63.0.3234.0")
            .setDomain("mcs.android.com")
            .setDeviceId("android-${credentials["gcm"]?.get("androidId")}")
            .setNetworkType(1)
            .setResource(credentials["gcm"]?.get("androidId") ?: "")
            .setUser(credentials["gcm"]?.get("androidId") ?: "")
            .setUseRmq2(true)
            .addSetting(Setting.newBuilder().setName("new_vc").setValue("1"))
            .addAllReceivedPersistentId(persistent_ids)
            .build()
        __send(s, req)
        login_response = __recv(s, first = True)
        while True:
        p = __recv(s)
        if type(p) is not DataMessageStanza :
        continue
        crypto_key = __app_data_by_key(p, "crypto-key")[3:]  # strip dh =
        salt = __app_data_by_key(p, "encryption")[5:]  # strip salt =
        crypto_key = urlsafe_b64decode(crypto_key.encode("ascii"))
        salt = urlsafe_b64decode(salt.encode("ascii"))
        der_data = credentials["keys"]["private"]
        der_data = urlsafe_b64decode(der_data.encode("ascii") + b"========")
        secret = credentials["keys"]["secret"]
        secret = urlsafe_b64decode(secret.encode("ascii") + b"========")
        privkey = load_der_private_key(
            der_data, password = None, backend = default_backend()
        )
        decrypted = http_ece.decrypt(
            p.raw_data, salt = salt,
            private_key = privkey, dh = crypto_key,
            version = "aesgcm",
            auth_secret = secret
        )
        callback(obj, json.loads(decrypted.decode("utf-8")), p)
    }

    /**
     * listens for push notifications
     *
     *   credentials: credentials object returned by register()
     *   callback(obj, notification, data_message): called on notifications
     *   received_persistent_ids: any persistent id's you already received.
     *                            array of strings
     */
    suspend fun listen(credentials: Map<String, String>, callback: (Map<String, String>, Map<String, String>, Map<String, String>) -> Unit, received_persistent_ids: List<String>) {
        val sock = withContext(Dispatchers.IO) {
            Socket(MTALK_HOST, 5228)
        }
        LoggingHelper.log(LoggingHelper.LogType.DEBUG, "connected to ssl socket")
        __listen(sock, credentials, callback, received_persistent_ids, obj)

    }


    /* --------------------------------------------------------------------------------- */

    private fun getASN1Dump(data: ByteArray): String {
        try {
            ASN1InputStream(ByteArrayInputStream(data)).use { asn1InputStream ->
                val asn1Primitive = asn1InputStream.readObject()
                return asn1Primitive.toString()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }

    private fun urandom(bytes: Int): ByteArray {
        val secureRandom = SecureRandom()
        val randomBytes = ByteArray(bytes)
        secureRandom.nextBytes(randomBytes)
        return randomBytes
    }
}*/