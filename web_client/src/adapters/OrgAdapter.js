import { loadOrganizationList, createOrganization, createService } from '../api/orgApi'
import { makeAutoObservable } from 'mobx'
import authAdapter from './AuthAdapter'
import parseOrganizations from '../parsers/parseOrganizations'

class OrgAdapter {
    constructor() {
        this.loadOrganizations()
        
        this.organizations = []

        makeAutoObservable(this)
    }

    loadOrganizations() {
        if (authAdapter.token) {
            loadOrganizationList(authAdapter.token)
                .then((data) => {
                    if (data.status === 200) {
                        this.setOrganizations(parseOrganizations(data.data))
                    }
                })
        }
    }

    addOrganization(name, address) {
        return createOrganization(authAdapter.token, name, address)
            .then(data => {
                this.loadOrganizations()
            })
    }

    addService(organizationId, name) {
        return createService(authAdapter.token, name, organizationId)
            .then(data => {
                this.loadOrganizations()
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
