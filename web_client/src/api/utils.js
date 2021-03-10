import backendUrl from './backendUrl'

export const withBackendUrl = (paths) => {
    for (let key of Object.keys(paths)) {
        paths[key] = backendUrl + paths[key]
    }
    console.log(paths)
    return paths
}
