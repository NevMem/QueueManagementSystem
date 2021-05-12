package com.nevmem.qms.push.service

import com.nevmem.qms.push.component.WorkerComponent
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct

@Service
class WorkManager @Autowired constructor(
    private val runners: List<WorkerComponent>
) {
    @PostConstruct
    fun postConstruct() {
        runners.forEach {
            it.run()
        }
    }
}
