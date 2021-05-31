package com.nevmem.qms.push.data

data class MessageQueueEntity(
    val target: String,
    val timeLeft: Int,
    val position: Int,
    val from: String
) {
    fun asMap(): Map<String, String> {
        return mapOf(
            "timeLeft" to timeLeft.toString(),
            "position" to position.toString(),
            "messageFrom" to from
        )
    }

    val notificationConfig: NotificationConfig
        get() {
            return NotificationConfig("Your appointment is soon", "You're ${position + 1} in the line")
        }
}
