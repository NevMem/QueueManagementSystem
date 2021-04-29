import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    currentTicket: '/admin/get_current_ticket',
    endServicing: '/admin/end_servicing',
    serviceNext: '/admin/service_next_user',
})

export const currentTicket = (token) => {
    return authorizedRequestWrapper(axios.post(
        paths.currentTicket,
        {},
        { headers: { session: token } }
    ))
}

export const nextUser = (token, windowName, serviceIds) => {
    return authorizedRequestWrapper(
        axios.post(
            paths.serviceNext,
            { window: windowName, serviceIds: serviceIds },
            { headers: { session: token } }))
}

export const endServicing = (token, resolution) => {
    return authorizedRequestWrapper(
        axios.post(
            paths.endServicing,
            { resolution: resolution },
            { headers: { session: token } }))
}
