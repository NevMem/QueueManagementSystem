import { defaultRequestWrapper } from './wrappers'
import axios from 'axios'
import backendUrl from './backendUrl'

export const processLogin = (login, password) => {
    return defaultRequestWrapper(axios.post(
        backendUrl + '/client/login',
        { email: login, password: password }))
}

export const loadUser = (token) => {
    return defaultRequestWrapper(axios.post(
        backendUrl + '/client/get_user',
        undefined,
        { headers: { session: token } }))
}
