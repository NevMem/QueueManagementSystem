package com.nevmem.qms.push.service

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.DeleteMessageRequest
import com.amazonaws.services.sqs.model.Message
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import com.google.gson.Gson
import com.nevmem.qms.push.data.MessageQueueEntity
import org.springframework.stereotype.Service

@Service
class MessagingService {

    private val queueName = "qms-notification"

    private val gson = Gson()

    private val client: AmazonSQS by lazy {
        val result = AmazonSQSClientBuilder.standard()
        result.credentials = EnvironmentVariableCredentialsProvider()
        result.setEndpointConfiguration(
            AwsClientBuilder.EndpointConfiguration(
                "https://message-queue.api.cloud.yandex.net/b1gh53sl59cbf7qr0bui/dj6000000001rhgs00qm/qms-notification",
                "ru-central1"))
        result.build()
    }

    private val queueUrl by lazy {
        client.getQueueUrl(queueName).queueUrl
    }

    fun getMessages(): List<Pair<Message, MessageQueueEntity>> {
        val request = ReceiveMessageRequest(queueUrl).apply {
            waitTimeSeconds = 10
        }

        return client.receiveMessage(request).messages.mapNotNull {
            println(it.body)
            try {
                it to gson.fromJson(it.body, MessageQueueEntity::class.java)
            } catch (exception: Exception) {
                println(exception.message)
                null
            }
        }
    }

    fun removeMessages(messages: List<Message>) {
        messages.forEach {
            val request = DeleteMessageRequest(queueUrl, it.receiptHandle)
            client.deleteMessage(request)
        }
    }

}
