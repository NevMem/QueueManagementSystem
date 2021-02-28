import './OrganizationCard.css'
import { Fragment, useState } from 'react'
import { withStyles } from '@material-ui/core/styles'
import AddButton from '../../components/buttons/add_button/AddButton'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import Grid from '@material-ui/core/Grid'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import Typography from '@material-ui/core/Typography'
import AddServiceDialog from '../dialogs/AddServiceDialog'
import AddQueueDialog from '../dialogs/AddQueueDialog'

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

const AddQueueGroup = ({service, ...rest}) => {
    const [open, setOpen] = useState(false)
    const handleOpen = () => { setOpen(true) }
    const handleClose = () => { setOpen(false) }
    return (
        <Fragment>
            <AddButton onClick={handleOpen} isPrimaryButton={false} text='Новая очередь' {...rest} />
            <AddQueueDialog open={open} onClose={handleClose} service={service} />
        </Fragment>
    )
}

const AddQueueRow = ({service, ...rest}) => {
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}} justify='center' {...rest}>
            <AddQueueGroup service={service} style={{display: 'inline-block'}} />
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
                    <AddQueueRow service={serviceData} />
                </AccordionDetails>
            </Accordion>
        </Fragment>
    )
}

export default function OrganizationCard({organizationData, ...props}) {
    const { name, services } = organizationData

    const [open, setOpen] = useState(false)

    const handleOpen = () => {
        setOpen(true)
    }

    const handleClose = () => {
        setOpen(false)
    }

    return (
        <div className = 'organizationCard' {...props}>
            <Grid container justify='space-between'>
                <Grid item xs={4}>
                    <Typography style={{color: '#c0c0c0', fontSize: '30px'}} variant='body2'>
                        {name}
                    </Typography>
                </Grid>
                <Grid item xs={2}>
                    <AddButton onClick={handleOpen} isPrimaryButton={false} text='Новый сервис' />
                    <AddServiceDialog open={open} onClose={handleClose} organization={organizationData} />
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
