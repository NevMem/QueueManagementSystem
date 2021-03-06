import { Fragment } from 'react'
import { withStyles } from '@material-ui/core/styles'
import AddQueueRow from '../queue-row/AddQueueRow'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import Grid from '@material-ui/core/Grid'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import QueueRow from '../queue-row/QueueRow'
import Typography from '@material-ui/core/Typography'

const Accordion = withStyles({
    root: {
        border: '1px solid rgba(0, 0, 0, .125)',
        backgroundColor: 'rgba(0, 0, 0, 0)',
        boxShadow: 'none',
        '&:not(:last-child)': {
            borderBottom: 0,
        },
        '&:before': {
            display: 'none',
        },
        '&$expanded': {
            margin: 'auto',
        },
    },
    expanded: {},
})(MuiAccordion)
  
const AccordionSummary = withStyles({
    root: {
        backgroundColor: 'rgba(0, 0, 0, .1)',
        borderBottom: '1px solid rgba(0, 0, 0, .125)',
        marginBottom: -1,
        minHeight: 56,
        '&$expanded': {
            minHeight: 56,
        },
    },
    content: {
        '&$expanded': {
            margin: '12px 0',
        },
    },
    expanded: {},
})(MuiAccordionSummary)
  
const AccordionDetails = withStyles((theme) => ({
    root: {
        padding: theme.spacing(2),
        flexDirection: 'column'
    },
}))(MuiAccordionDetails)

const ServiceRow = ({ serviceData }) => {
    return (
        <Fragment>
            <Accordion>
                <AccordionSummary
                    expandIcon={<ExpandMoreIcon />}
                    aria-controls={serviceData.id + '_control'}
                    id={serviceData.id + '_key'}
                >
                    <Grid container style={{paddingLeft: '16px', marginTop: '4px'}}>
                        <Grid item xs={2}>
                            <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>
                                {serviceData.name}
                            </Typography>
                        </Grid>
                        <Grid item xs={4}>
                            <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>
                                {serviceData.waiting}
                            </Typography>
                        </Grid>
                        <Grid item xs={3}>
                            <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>
                                {serviceData.avgServiceTime}
                            </Typography>
                        </Grid>
                        <Grid item xs={3}>
                            <Typography style={{color: '#a0a0a0', fontSize: '20px', textAlign: 'right'}} variant='body2'>
                                {serviceData.rating}
                            </Typography>
                        </Grid>
                    </Grid>
                </AccordionSummary>
                <AccordionDetails>
                    {serviceData.queues && serviceData.queues.map(queue => {                    
                        return <QueueRow key={queue.id} queueData={queue} />
                    })}
                    <AddQueueRow service={serviceData} />
                </AccordionDetails>
            </Accordion>
        </Fragment>
    )
}

export default ServiceRow