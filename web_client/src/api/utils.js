import backendUrl, { feedbackBackendUrl } from './backendUrl'

export const withBackendUrl = (paths, useJavaBackend = false) => {
    const prefix = useJavaBackend ? feedbackBackendUrl : backendUrl

    for (let key of Object.keys(paths)) {
        paths[key] = prefix + paths[key]
    }
    console.log(paths)
    return paths
}

/**
 * Retries request after each error (without delay)
 * @param requestFactory - factory of network requests Promise
 */
export const infiniteRetry = (requestFactory) => {
    return new Promise(async (res, rej) => {
        while (true) {
            const promise = requestFactory()
            try {
                const data = await promise
                res(data)
            } catch(err) {
                if (!err.isAxiosError) {
                    rej(err)
                }
            }
        }
    })
}
