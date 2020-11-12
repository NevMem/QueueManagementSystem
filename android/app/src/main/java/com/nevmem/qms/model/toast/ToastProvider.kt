package com.nevmem.qms.model.toast

import kotlinx.coroutines.flow.Flow

interface ToastProvider {
    val toastFlow: Flow<ToastData>
}
