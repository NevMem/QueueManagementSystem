package com.nevmem.qms.feedback

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FeedbackServer

fun main(args: Array<String>) {
    runApplication<FeedbackServer>(*args)
}
