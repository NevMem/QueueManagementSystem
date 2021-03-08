import authAdapter from '../adapters/AuthAdapter'

const logWrapper = (promise) => {
    return promise.then(data => { console.log('Response: ' + JSON.stringify(data)); return data; })
}

const unauthorizedWrapper = (promise) => {
    return promise
        .catch(err => {
            if (err.response.status == 403) {
                authAdapter.dropUserData()
            }
        })
}

export const defaultRequestWrapper = (promise) => {
    return logWrapper(promise)
}

export const authorizedRequestWrapper = (promise) => {
    return unauthorizedWrapper(defaultRequestWrapper(promise))
}
