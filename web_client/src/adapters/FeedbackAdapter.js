import { loadRating } from '../api/feedbackApi'
import authAdapter from './AuthAdapter'

class FeedbackAdapter {
    loadRating(entityId) {
        return loadRating(authAdapter.token, entityId)
    }
}

const feedbackAdapter = new FeedbackAdapter()
export default feedbackAdapter
