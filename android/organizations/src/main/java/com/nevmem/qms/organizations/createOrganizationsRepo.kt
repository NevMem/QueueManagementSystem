package com.nevmem.qms.organizations

import com.nevmem.qms.network.NetworkManager
import com.nevmem.qms.organizations.internal.OrganizationsRepoImpl

fun createOrganizationsRepo(
    networkManager: NetworkManager
): OrganizationsRepo = OrganizationsRepoImpl(networkManager)
