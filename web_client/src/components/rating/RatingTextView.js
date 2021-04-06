import localizedString from '../../localization/localizedString'
import Typography from '@material-ui/core/Typography'

export default function RatingTextView({ score, ...props }) {
    var color = '#A0A0A0'
    if (score < 4) {
        color = '#F45151'
    }
    if (score > 4.5) {
        color = '#4CAF50'
    }

    return (
        <Typography variant='body2' {...props} style={{...props.style, color: color}}>
            {localizedString('rating')} {score}
        </Typography>
    )
}
