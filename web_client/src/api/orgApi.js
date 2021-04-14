import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    addManagerToOrganization: '/admin/add_user',
    addManagerToService: '/admin/add_user',
    createOrganization: '/admin/create_organization',
    createService: '/admin/create_service',
    organizationsList: '/admin/get_organizations_list',
    queueTickets: '/admin/queue_tickets',
    updateManageOfOrganizationPrivilege: '/admin/update_user_privilege',
    updateManageOfServicePrivilege: '/admin/update_user_privilege',
    updateOrganization: '/admin/update_organization',
    updateService: '/admin/update_service',
})

export const loadOrganizationList = (token) => {
    return authorizedRequestWrapper(axios.post(
        paths.organizationsList,
        undefined,
        { headers: { session: token } }))
}

export const createOrganization = (token, name, address, data) => {
    return authorizedRequestWrapper(axios.post(
        paths.createOrganization,
        { name: name, address: address, data: data },
        { headers: { session: token } }))
}

export const updateOrganization = (token, id, name, address, data) => {
    return authorizedRequestWrapper(axios.post(
        paths.updateOrganization,
        { id: id, name: name, address: address, data: data },
        { headers: { session: token } }
    ))
}

export const createService = (token, name, organizationId, data) => {
    return authorizedRequestWrapper(axios.post(
        paths.createService,
        { name: name, organizationId: organizationId, data: data },
        { headers: { session: token } }))
}

export const updateService = (token, id, name, organizationId, data) => {
    return authorizedRequestWrapper(axios.post(
        paths.updateService,
        { name: name, organizationId: organizationId, id: id, data: data },
        { headers: { session: token } }))
}

export const addManagerToService = (token, serviceId, managerEmail) => {
    return authorizedRequestWrapper(axios.post(
        paths.addManagerToService,
        { permissionType: 'MANAGER', targetObject: 'SERVICE', id: serviceId, email: managerEmail },
        { headers: { session: token } }))
}

export const addManagerToOrganization = (token, organizationId, managerEmail) => {
    return authorizedRequestWrapper(axios.post(
        paths.addManagerToOrganization,
        { permissionType: 'MANAGER', targetObject: 'ORGANIZATION', id: organizationId, email: managerEmail },
        { headers: { session: token } }))
}

export const queueTickets = (token, organizationId) => {
    return authorizedRequestWrapper(axios.post(
        paths.queueTickets,
        { id: organizationId },
        { headers: { session: token } }))
}
