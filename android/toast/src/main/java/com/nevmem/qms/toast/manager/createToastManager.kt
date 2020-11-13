package com.nevmem.qms.toast.manager

import com.nevmem.qms.toast.manager.internal.ToastManagerImpl

fun createToastManager(): ToastManager = ToastManagerImpl()
