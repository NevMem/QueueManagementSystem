import {
    loadOrganizationList,
    createOrganization,
    createService,
    addManagerToService,
    updateOrganization
} from '../api/orgApi'
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
        if (authAdapter.user && authAdapter.user.token && !this.isLoading) {
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

    addOrganization(name, address, data) {
        return createOrganization(authAdapter.token, name, address, data)
            .then(data => {
                this.loadOrganizations()
            })
    }

    updateOrganization(id, name, address, data) {
        return updateOrganization(authAdapter.token, id, name, address, data)
            .then(data => {
                this.loadOrganizations()
            })
    }

    addService(organizationId, name, data) {
        return createService(authAdapter.token, name, organizationId, data)
            .then(() => {
                this.loadOrganizations()
            })
    }

    addManagerToService(serviceId, managerEmail) {
        return addManagerToService(authAdapter.token, serviceId, managerEmail)
            .then(() => {
                this.loadOrganizations()
            })
    }

    setOrganizations(orgs) {
        console.log(orgs)
        this.organizations = orgs
    }

    getOrganizations() {
        return this.organizations
    }
}

const orgAdapter = new OrgAdapter()
export default orgAdapter
