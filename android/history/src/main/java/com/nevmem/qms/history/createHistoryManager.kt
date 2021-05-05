package com.nevmem.qms.history

import com.nevmem.qms.auth.AuthManager
import com.nevmem.qms.history.internal.HistoryManagerImpl
import com.nevmem.qms.history.internal.usecase.ResolutionUsecaseFactory
import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.organizations.OrganizationsRepo
import kotlinx.coroutines.GlobalScope

fun createHistoryManager(
    authManager: AuthManager,
    networkManager: NetworkManager,
    organizationsRepo: OrganizationsRepo
): HistoryManager = HistoryManagerImpl(
    authManager,
    networkManager,
    ResolutionUsecaseFactory(GlobalScope, organizationsRepo)
)
