import { loadOrganizationList, createOrganization } from '../api/orgApi'
import authAdapter from './AuthAdapter'

const parseService = (json) => {
    if (!json.info || !json.info.id) {
        return undefined
    }
    const service = {
        id: json.info.id,
        name: json.info.name
    }
    return service
}

const parseServices = (json) => {
    if (!json) {
        return []
    }
    const services = []
    for (const serviceItem of json) {
        const parsed = parseService(serviceItem)
        if (parsed) {
            services.push(parsed)
        }
    }
    return services
}

const parseOrganization = (json) => {
    if (!json.info || !json.info.id) {
        return undefined
    }
    const organization = {
        id: json.info.id,
        name: json.info.name,
        services: parseServices(json.services)
    }
    return organization
}

const parseOrganizations = (json) => {
    if (!json.organisations) {
        return []
    }

    const organizations = []

    for (const orgItem of json.organisations) {
        const parsed = parseOrganization(orgItem)
        if (parsed) {
            organizations.push(parsed)
        }
    }

    return organizations
}

class OrgAdapter {
    constructor() {
        this.loadOrganizations()
        
        this.organizations = []
    }

    loadOrganizations() {
        loadOrganizationList(authAdapter.token)
            .then((data) => {
                if (data.status === 200) {
                    this.setOrganizations(parseOrganizations(data.data))
                }
            })
    }

    setOrganizations(orgs) {
        this.organizations = orgs
    }

    getOrganizations() {
        return this.organizations
    }
}

const orgAdapter = new OrgAdapter()
export default orgAdapter
