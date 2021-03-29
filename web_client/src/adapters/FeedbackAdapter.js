import { loadRating } from '../api/feedbackApi'
import authAdapter from './AuthAdapter'

class FeedbackAdapter {
    constructor() {
        this.cachedValues = {}
    }

    loadRating(entityId) {
        return new Promise((res, rej) => {
            if (entityId in this.cachedValues) {
                res(this.cachedValues[entityId])
            } else {
                loadRating(authAdapter.token, entityId)
                    .then(data => data.data)
                    .then(data => {
                        this.cachedValues[entityId] = data
                        res(data)
                    })
            }
        })
    }
}

const feedbackAdapter = new FeedbackAdapter()
export default feedbackAdapter
