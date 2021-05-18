package com.nevmem.qms.push

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.metrics.export.prometheus.EnablePrometheusMetrics

@EnablePrometheusMetrics
@SpringBootApplication(scanBasePackages = ["com.nevmem.qms"])
class PushServer

fun main(args: Array<String>) {
    runApplication<PushServer>(*args)
}
