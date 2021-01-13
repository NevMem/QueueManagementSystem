package com.nevmem.qms.permissions.internal

import com.nevmem.qms.permissions.*
import java.util.concurrent.LinkedBlockingQueue

internal class PermissionsManagerImpl : PermissionsManager {

    private val queue = LinkedBlockingQueue<PermissionsRequest>()

    private var delegate: PermissionsDelegate? = null

    override val hasRequests: Boolean
        get() = queue.isNotEmpty()

    override fun popRequest(): PermissionsRequest = queue.poll()!!

    override fun requestPermissions(
        permissions: List<Permission>,
        callback: (PermissionsResponse) -> Unit
    ) {
        queue.add(PermissionsRequest(permissions, callback))
        delegate?.onHasNewPermissionsRequest()
    }

    override fun hasPermission(permission: Permission): Boolean{
        check(delegate != null)
        return delegate!!.hasPermission(permission)
    }

    override fun registerDelegate(delegate: PermissionsDelegate) {
        this.delegate = delegate
    }

    override fun removeDelegate(delegate: PermissionsDelegate) {
        check(this.delegate == delegate) { "Cannot remove not registered delegate" }
        this.delegate = null
    }

}
