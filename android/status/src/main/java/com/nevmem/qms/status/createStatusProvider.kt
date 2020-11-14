package com.nevmem.qms.status

import com.nevmem.qms.status.internal.DebugStatusProvider


fun createDebugStatusProvider(): StatusProvider = DebugStatusProvider()
