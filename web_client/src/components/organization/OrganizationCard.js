import './OrganizationCard.css'
import { withStyles } from '@material-ui/core/styles'
import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import AddButton from '../../components/buttons/add_button/AddButton'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import { Fragment } from 'react'

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
        display: 'block'
    },
}))(MuiAccordionDetails)

const QueueRow = ({ queueData }) => {
    const { name, waiting, avgServiceTime } = queueData
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px', flexBasis: '100%'}}>
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
                </AccordionDetails>
            </Accordion>
        </Fragment>
    )
}

export default function OrganizationCard({organizationData, ...props}) {
    const { name, services } = organizationData
    return (
        <div className = 'organizationCard' {...props}>
            <Grid container justify='space-between'>
                <Grid item xs={4}>
                    <Typography style={{color: '#c0c0c0', fontSize: '30px'}} variant='body2'>
                        {name}
                    </Typography>
                </Grid>
                <Grid item xs={2}>
                    <AddButton isPrimaryButton={false} text='Новый сервис' />
                </Grid>
            </Grid>
            <Grid container style={{marginTop: '20px', marginBottom: '20px'}}>
                <Grid item>
                    <Typography style={{color: '#a0a0a0', fontSize: '24px'}} variant='body2'>
                        {services.length > 0 && 'Сервисы:'}
                        {services.length === 0 && 'У вас нет сервисов в данной организации'}
                    </Typography>
                </Grid>
            </Grid>
            { services.map(elem => {
                return <ServiceRow key={elem.id} serviceData={elem} />
            }) }
        </div>
    )
}