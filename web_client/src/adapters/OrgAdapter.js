import { loadOrganizationList, createOrganization, createService } from '../api/orgApi'
import { makeAutoObservable, autorun } from 'mobx'
import authAdapter from './AuthAdapter'
import parseOrganizations from '../parsers/parseOrganizations'

class OrgAdapter {
    constructor() {
        this.loadOrganizations()
        
        this.organizations = []

        this.isLoading = false

        autorun(() => {
            if (authAdapter.token) {
                this.loadOrganizations()
            }
        })

        makeAutoObservable(this)
    }

    get loading() {
        return this.isLoading
    }

    loadOrganizations() {
        if (authAdapter.token && !this.isLoading) {
            this.isLoading = true
            loadOrganizationList(authAdapter.token)
                .then((data) => {
                    if (data.status === 200) {
                        this.setOrganizations(parseOrganizations(data.data).reverse())
                    }
                    this.isLoading = false
                })
                .catch(err => {
                    this.isLoading = false
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
