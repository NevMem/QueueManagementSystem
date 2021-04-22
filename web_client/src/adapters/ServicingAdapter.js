import authAdapter from './AuthAdapter'
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
        makeAutoObservable(this)

        this.scheduleUpdate()
    }

    scheduleUpdate() {
        currentTicket(authAdapter.token)
            .then(data => data.data)
            .then(data => {
                this.currentTicket = data
                this.rescheduleUpdate()
            })
            .catch(() => {
                this.currentTicket = undefined
                this.rescheduleUpdate()
            })
    }

    nextUser(windowName, serviceIds) {
        return nextUser(authAdapter.token, windowName, serviceIds)
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

    addServiceToServicing(serviceId) {
        const newServiceIds = [...this.serviceIds]
        newServiceIds.push(serviceId)
        this.serviceIds = newServiceIds
    }

    removeServiceFromServicing(serviceId) {
        const newServiceIds = [...this.serviceIds]
        const index = newServiceIds.indexOf(serviceId)
        if (index >= 0) {
            newServiceIds.splice(index, 1)
            this.serviceIds = newServiceIds
        }
    }

    setWindowName(windowName) {
        this.windowName = windowName
    }
}

const servicingAdapter = new ServicingAdapter()
export default servicingAdapter
