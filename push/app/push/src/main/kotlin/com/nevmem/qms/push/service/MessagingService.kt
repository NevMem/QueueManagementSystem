package com.nevmem.qms.push.service

import com.amazonaws.auth.EnvironmentVariableCredentialsProvider
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.sqs.AmazonSQS
import com.amazonaws.services.sqs.AmazonSQSClientBuilder
import com.amazonaws.services.sqs.model.ReceiveMessageRequest
import org.springframework.stereotype.Service

@Service
class MessagingService {

    private val queueName = "qms-notification"

    val client: AmazonSQS by lazy {
        val result = AmazonSQSClientBuilder.standard()
        result.credentials = EnvironmentVariableCredentialsProvider()
        result.setEndpointConfiguration(
            AwsClientBuilder.EndpointConfiguration("smth", "ru-central1"))
        result.build()
    }

    fun getMessages() {
        val queueUrl = client.getQueueUrl(queueName).queueUrl

        val request = ReceiveMessageRequest(queueUrl).apply {
            maxNumberOfMessages = 10
        }

        client.receiveMessage(request).messages.forEach {
            println(it.body)
        }
    }

}
