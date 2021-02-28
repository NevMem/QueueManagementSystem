import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'

const QueueRow = ({ queueData }) => {
    const { name, waiting, avgServiceTime } = queueData
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}}>
            <Grid item xs={2}>
                <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>
                    {name}
                </Typography>
            </Grid>
            <Grid item xs={4}>
                <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>
                    {waiting}
                </Typography>
            </Grid>
            <Grid item xs={5}>
                <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>
                    {avgServiceTime}
                </Typography>
            </Grid>
        </Grid>
    )
}

export default QueueRow
