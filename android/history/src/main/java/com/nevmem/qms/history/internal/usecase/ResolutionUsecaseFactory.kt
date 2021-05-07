package com.nevmem.qms.history.internal.usecase

import com.nevmem.qms.organizations.OrganizationsRepo
import kotlinx.coroutines.CoroutineScope

internal class ResolutionUsecaseFactory(
    private val coroutineScope: CoroutineScope,
    private val organizationsRepo: OrganizationsRepo
) {
    fun createUsecase() = ResolutionUsecase(coroutineScope, organizationsRepo)
}
