export const logWrapper = (promise) => {
    return promise.then(data => { console.log('Response: ' + JSON.stringify(data)); return data; })
}

export const defaultRequestWrapper = (promise) => {
    return logWrapper(promise)
}
