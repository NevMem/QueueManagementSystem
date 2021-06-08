import authAdapter from './AuthAdapter'
import orgAdapter from './OrgAdapter'
import { makeAutoObservable } from 'mobx'
import {
    currentTicket,
    nextUser,
    endServicing,
} from '../api/servicingApi'

class ServicingAdapter {
    constructor() {
        this.currentTicket = undefined
        this.serviceIds = []
        this.windowName = ''
        this.filteredTickets = []
        makeAutoObservable(this)

        this.scheduleUpdate()

        this.loadState()
    }

    saveState() {
        localStorage.setItem('service_ids', JSON.stringify(this.serviceIds))
        localStorage.setItem('window_name', JSON.stringify(this.windowName))
    }

    loadState() {
        this.serviceIds = JSON.parse(localStorage.getItem('service_ids'))
        this.windowName = JSON.parse(localStorage.getItem('window_name'))
        if (this.serviceIds === null || this.serviceIds === undefined) {
            this.serviceIds = []
        }
        console.log(this.serviceIds)
    }

    scheduleUpdate() {
        if (authAdapter.token === undefined || authAdapter.token === null) {
            return
        }
        const ticketPromise = currentTicket(authAdapter.token)
            .then(data => data.data)
        var organizationId = undefined
        if (this.serviceIds.length > 0) {
            organizationId = this.serviceIds[0].organizationId
        }
        var ticketsPromise = new Promise((res, rej) => { res([]) })
        if (organizationId !== undefined) {
            ticketsPromise = orgAdapter.tickets(organizationId)
        }
        Promise.allSettled([ticketPromise, ticketsPromise])
            .then(values => {
                const ticket = values[0]
                const tickets = values[1].value
                this.currentTicket = ticket.value
                if (tickets !== undefined && tickets.tickets !== undefined) {
                    this.filteredTickets = tickets.tickets.filter(elem => {
                        return this.serviceIds.map(elem => elem.serviceId).indexOf(elem.serviceId) >= 0
                    })
                }
                this.rescheduleUpdate()
            })
    }

    nextUser() {
        return nextUser(authAdapter.token, this.windowName, this.serviceIds.map(elem => elem.serviceId))
            .then(data => data.data)
            .then(data => {
                this.rescheduleUpdate(true)
                return data
            })
    }

    endServicing(resolution) {
        return endServicing(authAdapter.token, resolution)
            .then(data => data.data)
            .then(data => {
                this.rescheduleUpdate(true)
                return data
            })
    }

    rescheduleUpdate(force) {
        if (force) {
            this.scheduleUpdate()
        } else {
            setTimeout(this.scheduleUpdate.bind(this), 1000)
        }
    }

    addServiceToServicing(organizationId, serviceId) {
        let newServiceIds = [...this.serviceIds]
        newServiceIds = newServiceIds.filter(elem => elem.organizationId === organizationId)
        newServiceIds.push({ organizationId: organizationId, serviceId: serviceId })
        this.serviceIds = newServiceIds
        this.saveState()
    }

    removeServiceFromServicing(serviceId) {
        const newServiceIds = [...this.serviceIds]
        const index = newServiceIds.indexOf(newServiceIds.find(elem => elem.serviceId === serviceId))
        if (index >= 0) {
            newServiceIds.splice(index, 1)
            this.serviceIds = newServiceIds
        }
        this.saveState()
    }

    setWindowName(windowName) {
        this.windowName = windowName
        this.saveState()
    }
}

const servicingAdapter = new ServicingAdapter()
export default servicingAdapter
