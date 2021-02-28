import axios from 'axios'
import backendUrl from './backendUrl'
import { defaultRequestWrapper } from './wrappers'

export const processLogin = (login, password) => {
    return defaultRequestWrapper(axios.post(backendUrl + '/client/login', { email: login, password: password }))
    /* return new Promise((res, rej) => {
        res({headers: {session: 'some-session'}})
    }) */
}

export const loadUser = (token) => {
    return defaultRequestWrapper(axios.post(backendUrl + '/client/get_user'), undefined, {headers: {session: token}})
    /* return new Promise((res, rej) => {
        res({data: {email: 'memlolkek@gmail.com', name: 'Name', surname: 'Surname'}, code: 200})
    }) */
}
