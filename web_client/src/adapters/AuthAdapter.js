import { makeAutoObservable } from 'mobx'
import { processLogin, processRegistration, loadUser, checkAuth } from '../api/authApi'

class User {
    constructor(name, surname, login, token, wholeUserJson) {
        this.name = name
        this.surname = surname
        this.login = login
        this.token = token
        this.wholeUserJson = wholeUserJson
    }
}

class AuthAdapter {
    constructor() {
        this.token = undefined
        this.user = undefined

        this.loadFromStorage()

        makeAutoObservable(this)
    }

    loadFromStorage() {
        this.token = localStorage.getItem('token')
        const savedUser = localStorage.getItem('user')
        if (savedUser) {
            const jsonUser = JSON.parse(savedUser)
            this.user = new User(
                jsonUser['name'],
                jsonUser['surname'],
                jsonUser['login'],
                jsonUser['token'],
                jsonUser['wholeUserJson'])
            console.log('loaded user from local storage')
            this.checkAuth()
        }
    }

    currentUser() {
        return this.user
    }

    saveUser() {
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
    }

    checkAuth() {
        checkAuth(this.token)
            .then((data) => {
                if (data) {
                    console.log('Auth checked')
                }
            })
    }

    logout() {
        this.dropUserData()
    }

    dropUserData() {
        console.log('Dropping user data')
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        window.location.reload()
    }

    register(login, password, name, surname) {
        return new Promise((res, rej) => {
            processRegistration(login, password, name, surname)
                .then(() => {
                    return this.login(login, password)
                })
                .then(() => {
                    res()
                })
                .catch(err => {
                    rej(err)
                })
        })
    }

    login(login, password) {
        return new Promise((res, rej) => {
            processLogin(login, password)
                .then(data => {
                    const token = data.headers['session']
                    this.token = token
                    return loadUser(token)
                })
                .then(resp => {
                    const { email, name, surname } = resp.data
                    if (resp.status === 200) {
                        this.user = new User(name, surname, email, this.token, resp.data)
                        this.saveUser()
                        res()
                    } else {
                        throw(new Error('Response code isn\'t 200'))
                    }
                })
                .catch(err => {
                    console.log(err)
                    rej(err.message)
                })
        })
    }
}

const authAdapter = new AuthAdapter()
export default authAdapter
