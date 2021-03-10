import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    createOrganization: '/admin/create_organisation',
    organizationsList: '/admin/get_organisations_list',
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

export const createService = (token, name, organizationId) => {
    return authorizedRequestWrapper(axios.post(
        paths.createService,
        { name: name, organisationId: organizationId },
        { headers: { session: token } }))
}
