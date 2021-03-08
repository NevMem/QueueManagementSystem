import { defaultRequestWrapper, authorizedRequestWrapper } from './wrappers'
import axios from 'axios'
import backendUrl from './backendUrl'

const withBackendUrl = (paths) => {
    for (let key of Object.keys(paths)) {
        paths[key] = backendUrl + paths[key]
    }
    console.log(paths)
    return paths
}

const paths = withBackendUrl({
    checkAuth: '/check_auth',
    getUser: '/client/get_user',
    login: '/client/login',
    register: '/client/register',
})

export const processLogin = (login, password) => {
    return defaultRequestWrapper(axios.post(
        paths.login,
        { email: login, password: password }))
}

export const loadUser = (token) => {
    return defaultRequestWrapper(axios.post(
        paths.getUser,
        undefined,
        { headers: { session: token } }))
}

export const checkAuth = (token) => {
    return authorizedRequestWrapper(
        axios.post(paths.checkAuth, undefined, { headers: { session: token } }))
}
