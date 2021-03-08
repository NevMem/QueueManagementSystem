import { processLogin, loadUser } from '../api/authApi'

class User {
    constructor(name, surname, login, token) {
        this.name = name
        this.surname = surname
        this.login = login
        this.token = token
    }
}

class AuthAdapter {
    constructor() {
        this.token = undefined
        this.user = undefined

        this.loadFromStorage()
    }

    loadFromStorage() {
        this.token = localStorage.getItem('token')
        const savedUser = localStorage.getItem('user')
        if (savedUser) {
            const jsonUser = JSON.parse(savedUser)
            this.user = new User(jsonUser['name'], jsonUser['surname'], jsonUser['login'], jsonUser['token'])
            console.log('loaded user from local storage')
        }
    }

    currentUser() {
        return this.user
    }

    saveUser() {
        localStorage.setItem('token', this.token)
        localStorage.setItem('user', JSON.stringify(this.user))
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
                        this.user = new User(name, surname, email, this.token)
                        this.saveUser()
                        res()
                    } else {
                        throw(new Error('Response code isn\'t 200'))
                    }
                })
                .catch(err => {
                    rej(err.message)
                })
        })
    }
}

const authAdapter = new AuthAdapter()
export default authAdapter
