import { Fragment, Component } from 'react'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import feedbackAdapter from '../../adapters/FeedbackAdapter'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import localizedString from '../../localization/localizedString'
import RatingTextView from '../../components/rating/RatingTextView'
import Typography from '@material-ui/core/Typography'

const FeedbackLoadingRow = ({ ...props }) => {
    return (
        <Card {...props}>
            <CardContent style={{paddingBottom: '16px'}}>
                <Typography variant='h6'>{localizedString('loading')}</Typography>
            </CardContent>
        </Card>
    )
}

const FeedbackRow = ({ feedback, ...props }) => {
    return (
        <Card {...props}>
            <CardContent style={{paddingBottom: '16px'}}>
                <Typography variant='h6'>{feedback.text}</Typography>
                <Typography variant='body2'>{feedback.author}</Typography>
                <RatingTextView score={feedback.score} style={{marginTop: '4px'}} />
            </CardContent>
        </Card>
    )
}

export default class FeedbackPage extends Component {

    constructor(prps) {
        super(prps)
        this.state = {
            entityId: this.props.location.pathname.split('/').pop(),
            loading: true,
            feedback: []
        }
    }

    componentDidMount() {
        feedbackAdapter.loadFeedback(this.state.entityId)
            .then(data => {
                console.log(data)
                this.setState(state => { return { ...state, loading: false, feedback: data } })
            })
    }

    render() {
        return (
            <Fragment>
                <Header />
                <Grid container justify='center'>
                    <Grid item xs={8}>
                        { this.state.feedback.map((elem, index) => {
                            return (
                                <FeedbackRow
                                    key={index}
                                    style={{
                                        marginTop: '16px',
                                        width: '100%',
                                        backgroundColor: 'rgba(0, 132, 173, 0.07)'
                                    }}
                                    variant='outlined'
                                    feedback={elem} />
                            )
                        })}
                        { this.state.loading &&
                            <FeedbackLoadingRow
                                variant='outlined'
                                style={{
                                    marginTop: '16px',
                                    width: '100%',
                                    backgroundColor: 'rgba(0, 132, 173, 0.07)'
                                }} />
                        }
                    </Grid>
                </Grid>
            </Fragment>
        )
    }
}
