const logWrapper = (promise) => {
    return promise.then(data => { console.log('Response: ' + JSON.stringify(data)); return data; })
}

const unauthorizedWrapper = (promise) => {
    return promise.then(data => {
        console.log('Unauhtorized')
        if (data.status == 403) {
            window.location.reload()
        }
        return data
    })
}

export const defaultRequestWrapper = (promise) => {
    return logWrapper(promise)
}

export const authorizedRequestWrapper = (promise) => {
    return unauthorizedWrapper(defaultRequestWrapper(promise))
}
