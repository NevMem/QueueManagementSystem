import { authorizedRequestWrapper } from './wrappers'
import { withBackendUrl } from './utils'
import axios from 'axios'

const paths = withBackendUrl({
    currentTicket: '/admin/get_current_ticket',
})

export const currentTicket = (token) => {
    return authorizedRequestWrapper(axios.post(
        paths.currentTicket,
        {},
        { headers: { session: token } }
    ))
}
