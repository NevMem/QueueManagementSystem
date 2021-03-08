import { defaultRequestWrapper, authorizedRequestWrapper } from './wrappers'
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

export const checkAuth = (token) => {
    return authorizedRequestWrapper(
        axios.post(backendUrl + '/check_auth', undefined, { headers: { session: token } }))
}
