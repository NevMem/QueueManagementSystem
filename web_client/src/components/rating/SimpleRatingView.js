import { useState } from 'react'
import Typography from '@material-ui/core/Typography'
import feedbackAdapter from '../../adapters/FeedbackAdapter'
import localizedString from '../../localization/localizedString'

export default function SimpleRatingView({ entityId }) {
    const [ rating, setRating ] = useState(undefined)
    const [ loading, setLoading ] = useState(true)

    feedbackAdapter.loadRating(entityId)
        .then(data => data.data)
        .then(data => {
            setLoading(false)
            console.log(data)
            const rating = data.rating
            if (rating) {
                setRating(rating)
            } else {
                setRating(localizedString('no_rating_yet'))
            }
        })

    return (
        <Typography variant='body1'>
            { loading && 'Loading...'}
            {rating}
        </Typography>
    )
}