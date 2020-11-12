package com.nevmem.qms.model.toast

import com.nevmem.qms.model.toast.internal.ToastManagerImpl

fun createToastManager(): ToastManager = ToastManagerImpl()
