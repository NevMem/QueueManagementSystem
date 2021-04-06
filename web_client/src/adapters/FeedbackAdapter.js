import { loadRating, loadFeedback } from '../api/feedbackApi'
import authAdapter from './AuthAdapter'

const wrapPromise = (promise, res) => {
    promise.then(data => data.data)
        .then(data => res(data))
}

class FeedbackAdapter {
    constructor() {
        this.cachedRatings = {}
        this.cachedFeedbacks = {}
    }

    loadRating(entityId) {
        return new Promise((res, rej) => {
            if (entityId in this.cachedRatings) {
                this.cachedRatings[entityId]
                    .then(data => data.data)
                    .then(data => res(data))
            } else {
                const promise = loadRating(authAdapter.token, entityId)
                
                this.cachedRatings[entityId] = promise
                promise
                    .then(data => data.data)
                    .then(data => res(data))
            }
        })
    }

    loadFeedback(entityId) {
        return new Promise((res, rej) => {
            if (entityId in this.cachedFeedbacks) {
                wrapPromise(this.cachedFeedbacks[entityId], res)
            } else {
                const promise = loadFeedback(authAdapter.token, entityId)

                this.cachedFeedbacks[entityId] = promise
                wrapPromise(promise, res)
            }
        })
    }
}

const feedbackAdapter = new FeedbackAdapter()
export default feedbackAdapter
