/*package me.chicchi7393.registroapi.notification

import io.ktor.network.sockets.*
import io.ktor.utils.io.core.*
import me.chicchi7393.registroapi.notification.Constants.MCSVersion
import me.chicchi7393.registroapi.notification.Constants.minPacketLen
import me.chicchi7393.registroapi.notification.Constants.tagPacketLen
import me.chicchi7393.registroapi.notification.Constants.versionPacketLen
import java.io.File

class Parser {
    private var _messageTag: Int
    private var _messageSize: Int
    private var _handshakeComplete: Boolean
    private var _isWaitingForData: Boolean
    private var _sizePacketSoFar: Int
    private var _data: ByteArray
    private var _state: Constants.ProcessingState
    private var _socket: Socket
    fun DEBUG(message: String) =
        println("DEBUG: $message")

    var proto: File = File("protobuf/mcs.proto")

    constructor(socket: Socket) {
        this._socket = socket;
        this._state = Constants.ProcessingState.MCS_VERSION_TAG_AND_SIZE;
        this._data = ByteArray(0);
        this._sizePacketSoFar = 0;
        this._messageTag = 0;
        this._messageSize = 0;
        this._handshakeComplete = false;
        this._isWaitingForData = true;
        //this._onData = this._onData.bind(this);
        //this._socket.on('data', this._onData);
    }

    fun _onData(buffer: ByteArray) {
        DEBUG("Got data: ${buffer.size}");
        this._data += buffer
        if (this._isWaitingForData) {
            this._isWaitingForData = false;
            this._waitForData();
        }
    }
    fun   _waitForData() {
        DEBUG("waitForData state: ${this._state}");

        var minBytesNeeded = 0;

        minBytesNeeded = when (this._state) {
            Constants.ProcessingState.MCS_VERSION_TAG_AND_SIZE ->
                versionPacketLen + tagPacketLen + minPacketLen;
            Constants.ProcessingState.MCS_TAG_AND_SIZE ->
                tagPacketLen + minPacketLen;
            Constants.ProcessingState.MCS_SIZE ->
                this._sizePacketSoFar + 1;
            Constants.ProcessingState.MCS_PROTO_BYTES ->
                this._messageSize;
        }

        if (this._data.size < minBytesNeeded) {
            // TODO(ibash) set a timeout and check for socket disconnect
            DEBUG(
                "Socket read finished prematurely. Waiting for ${minBytesNeeded -
                    this._data.size} more bytes"
            );
            this._isWaitingForData = true;
            return;
        }

        DEBUG("Processing MCS data: state == ${this._state}");

        when (this._state) {
            Constants.ProcessingState.MCS_VERSION_TAG_AND_SIZE ->
                this._onGotVersion();
            Constants.ProcessingState.MCS_TAG_AND_SIZE ->
                this._onGotMessageTag();
            Constants.ProcessingState.MCS_SIZE ->
                this._onGotMessageSize();
            Constants.ProcessingState.MCS_PROTO_BYTES ->
                this._onGotMessageBytes();
        }
    }
    fun _onGotVersion() {
        val version = this._data[0];
        this._data = this._data.slice(1..<this._data.size).toByteArray();
        DEBUG("VERSION IS ${version}");

        if (version < MCSVersion && version.toInt() !== 38) {
            throw Exception("Got wrong version: $version")
        }

        // Process the LoginResponse message tag.
        this._onGotMessageTag();
    }
    fun _onGotMessageTag() {
        this._messageTag = this._data[0].toInt();
        this._data = this._data.slice(11..<this._data.size).toByteArray();
        DEBUG("RECEIVED PROTO OF TYPE ${this._messageTag}");

        this._onGotMessageSize();
    }

    fun _onGotMessageSize() {
        val incompleteSizePacket = false;
        val reader = new BufferReader(this._data);

        try {
            this._messageSize = reader.int32();
        } catch (error) {
            if (error.message.startsWith('index out of range:')) {
                incompleteSizePacket = true;
            } else {
                this._emitError(error);
                return;
            }
        }

        // TODO(ibash) in chromium code there is an extra check here of:
        // if prev_byte_count >= kSizePacketLenMax then something else went wrong
        // NOTE(ibash) I could only test this case by manually cutting the buffer
        // above to be mid-packet like: new BufferReader(this._data.slice(0, 1))
        if (incompleteSizePacket) {
            this._sizePacketSoFar = reader.pos;
            this._state = MCS_SIZE;
            this._waitForData();
            return;
        }

        this._data = this._data.slice(reader.pos);

        DEBUG(`Proto size: ${this._messageSize}`);
        this._sizePacketSoFar = 0;

        if (this._messageSize > 0) {
            this._state = MCS_PROTO_BYTES;
            this._waitForData();
        } else {
            this._onGotMessageBytes();
        }
    }
}*/