import authAdapter from './AuthAdapter'
import { makeAutoObservable } from 'mobx'
import {
    currentTicket
} from '../api/servicingApi'

class ServicingAdapter {
    constructor() {
        this.current_ticket = undefined
        makeAutoObservable(this)

        this.scheduleUpdate()
    }

    scheduleUpdate() {
        currentTicket(authAdapter.token)
            .then(data => data.data)
            .then(data => {
                this.current_ticket = data
                console.log(this.current_ticket)
                this.rescheduleUpdate()
            })
            .catch(() => {
                this.current_ticket = undefined
                this.rescheduleUpdate()
            })
    }

    rescheduleUpdate() {
        setTimeout(this.scheduleUpdate.bind(this), 1000)
    }
}

const servicingAdapter = new ServicingAdapter()
export default servicingAdapter
