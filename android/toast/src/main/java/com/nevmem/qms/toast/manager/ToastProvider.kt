package com.nevmem.qms.toast.manager

import com.nevmem.qms.toast.manager.ToastData
import kotlinx.coroutines.flow.Flow

interface ToastProvider {
    val toastFlow: Flow<ToastData>
}
