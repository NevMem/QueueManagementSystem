import { loadRating } from '../api/feedbackApi'
import authAdapter from './AuthAdapter'

class FeedbackAdapter {
    constructor() {
        this.cached = {}
    }

    loadRating(entityId) {
        return new Promise((res, rej) => {
            if (entityId in this.cached) {
                this.cached[entityId]
                    .then(data => data.data)
                    .then(data => res(data))
            } else {
                const promise = loadRating(authAdapter.token, entityId)
                
                this.cached[entityId] = promise
                promise
                    .then(data => data.data)
                    .then(data => res(data))
            }
        })
    }
}

const feedbackAdapter = new FeedbackAdapter()
export default feedbackAdapter
