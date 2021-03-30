import { authorizedRequestWrapper } from './wrappers'
import axios from 'axios'
import { withBackendUrl } from './utils'

const paths = withBackendUrl({
    loadRating: '/api/rating'
}, true)

export const loadRating = (token, entityId) => {
    return authorizedRequestWrapper(axios.post(
        paths.loadRating,
        { entityId: entityId },
        { headers: { session: token } }
    ))
}
