package me.chicchi7393.registroapi.utilities

object LoggingHelper {
    fun log(level: LogType, text: String) {
        println("$level, $text")
    }

    enum class LogType {
        DEBUG, INFO, WARNING, ERROR;
    }
}