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
}

const servicingAdapter = new ServicingAdapter()
export default servicingAdapter
