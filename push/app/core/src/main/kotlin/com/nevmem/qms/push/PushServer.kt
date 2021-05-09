package com.nevmem.qms.push

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PushServer

fun main(args: Array<String>) {
    runApplication<PushServer>(*args)
}
