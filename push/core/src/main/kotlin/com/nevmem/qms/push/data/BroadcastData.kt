package com.nevmem.qms.push.data

data class NotificationConfig(var title: String = "", var body: String = "")

data class BroadcastMessageConfig(
    var data: Map<String, String> = mapOf(),
    var notificationConfig: NotificationConfig? = null)
data class SpecifiedBroadcastMessageConfig(
    var tokens: List<String>,
    var data: Map<String, String> = mapOf(),
    var notificationConfig: NotificationConfig? = null)
data class SendToOneRequest(
    var email: String,
    var data: Map<String, String> = mapOf(),
    var notificationConfig: NotificationConfig? = null)
