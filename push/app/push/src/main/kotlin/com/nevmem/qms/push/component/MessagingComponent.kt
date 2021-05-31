package com.nevmem.qms.push.component

import com.nevmem.qms.push.service.FbPushService
import com.nevmem.qms.push.service.MessagingService
import com.nevmem.qms.push.service.TokenStorageService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.util.concurrent.Executors

@Component
class MessagingComponent @Autowired constructor(
    private val messaging: MessagingService,
    private val tokenStorageService: TokenStorageService,
    private val fbPushService: FbPushService
) : WorkerComponent() {

    private val executor = Executors.newSingleThreadExecutor()

    override fun run() {
        executor.submit {
            runLoop()
        }
    }

    private fun runLoop() {
        while (true) {
            try {
                val messages = messaging.getMessages()
                messaging.removeMessages(messages.map { it.first })
                messages.forEach { println("not resolved $it") }
                val resolvedMessages = messages.map {
                        val email = tokenStorageService.tokenByEmail(it.second.target)
                        email to it
                    }
                    .filter { it.first != null }
                    .map { it.first!! to it.second }
                resolvedMessages.forEach { println("resolved $it") }
                resolvedMessages.forEach {
                    fbPushService.sendToOne(
                        it.first,
                        it.second.second.asMap(),
                        it.second.second.notificationConfig)
                }
            } catch(exception: Exception) {
                println(exception.message)
                Thread.sleep(5000L)
            }
        }
    }
}
