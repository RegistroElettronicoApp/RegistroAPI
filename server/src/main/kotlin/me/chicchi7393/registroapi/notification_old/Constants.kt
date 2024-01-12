package me.chicchi7393.registroapi.notification_old

object Constants {
    enum class ProcessingState {
        // Processing the version, tag, and size packets (assuming minimum length
        // size packet). Only used during the login handshake.
        MCS_VERSION_TAG_AND_SIZE,

        // Processing the tag and size packets (assuming minimum length size
        // packet). Used for normal messages.
        MCS_TAG_AND_SIZE,

        // Processing the size packet alone.
        MCS_SIZE,

        // Processing the protocol buffer bytes (for those messages with non-zero
        // sizes).
        MCS_PROTO_BYTES,
    }

    // # of bytes a MCS version packet consumes.
    val versionPacketLen = 1

    // # of bytes a tag packet consumes.
    val tagPacketLen = 1

    // Max # of bytes a length packet consumes. A Varint32 can consume up to 5 bytes
    // (the msb in each byte is reserved for denoting whether more bytes follow).
    // Although the protocol only allows for 4KiB payloads currently, and the socket
    // stream buffer is only of size 8KiB, it's possible for certain applications to
    // have larger message sizes. When payload is larger than 4KiB, an temporary
    // in-memory buffer is used instead of the normal in-place socket stream buffer.
    val minPacketLen = 1
    val maxPacketLen = 5

    // The current MCS protocol version.
    val MCSVersion = 41

    // MCS Message tags.
    // WARNING: the order of these tags must remain the same, as the tag values
    // must be consistent with those used on the server.
    enum class MCSMessageTags {
        HeartbeatPingTag,
        HeartbeatAckTag,
        LoginRequestTag,
        LoginResponseTag,
        CloseTag,
        MessageStanzaTag,
        PresenceStanzaTag,
        IqStanzaTag,
        DataMessageStanzaTag,
        BatchPresenceStanzaTag,
        StreamErrorStanzaTag,
        HttpRequestTag,
        HttpResponseTag,
        BindAccountRequestTag,
        BindAccountResponseTag,
        TalkMetadataTag,
        NumProtoTypes;
    }
}