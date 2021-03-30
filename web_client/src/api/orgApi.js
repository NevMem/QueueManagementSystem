import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    addManagerToOrganization: '/admin/add_user',
    addManagerToService: '/admin/add_user',
    createOrganization: '/admin/create_organization',
    createService: '/admin/create_service',
    organizationsList: '/admin/get_organizations_list',
    updateManageOfOrganizationPrivilege: 'admin/update_user_privilege',
    updateManageOfServicePrivilege: 'admin/update_user_privilege',
})

export const loadOrganizationList = (token) => {
    return authorizedRequestWrapper(axios.post(
        paths.organizationsList,
        undefined,
        { headers: { session: token } }))
}

export const createOrganization = (token, name, address) => {
    return authorizedRequestWrapper(axios.post(
        paths.createOrganization,
        { name: name, address: address },
        { headers: { session: token } }))
}

export const createService = (token, name, organizationId, data) => {
    return authorizedRequestWrapper(axios.post(
        paths.createService,
        { name: name, organizationId: organizationId, data: data },
        { headers: { session: token } }))
}

export const addManagerToService = (token, serviceId, managerEmail) => {
    return authorizedRequestWrapper(axios.post(
        paths.addManagerToService,
        { permissionType: 'MANAGER', targetObject: 'SERVICE', id: serviceId, email: managerEmail },
        { headers: { session: token } }
    ))
}

export const addManagerToOrganization = (token, organizationId, managerEmail) => {
    return authorizedRequestWrapper(axios.post(
        paths.addManagerToOrganization,
        { permissionType: 'MANAGER', targetObject: 'ORGANIZATION', id: organizationId, email: managerEmail },
        { headers: { session: token } }
    ))
}
