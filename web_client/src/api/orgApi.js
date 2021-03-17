import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    createOrganization: '/admin/create_organization',
    createService: '/admin/create_service',
    organizationsList: '/admin/get_organizations_list',
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
        { name: name, organisationId: organizationId, data: data },
        { headers: { session: token } }))
}
