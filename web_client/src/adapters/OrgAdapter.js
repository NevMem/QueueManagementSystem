import { loadOrganizationList, createOrganization } from '../api/orgApi'
import authAdapter from './AuthAdapter'
import parseOrganizations from '../parsers/parseOrganizations'

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
