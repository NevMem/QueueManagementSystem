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
    }

    currentUser() {
        return this.user
    }

    login(login, password) {
        return new Promise((res, rej) => {
            processLogin(login, password)
                .then(data => {
                    const token = data.headers['session']
                    this.token = token
                    console.log(data.headers)
                    console.log(data)
                    return loadUser(token)
                })
                .then(resp => {
                    const { email, name, surname } = resp.data
                    if (resp.code === 200) {
                        this.user = new User(name, surname, email, this.token)
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
