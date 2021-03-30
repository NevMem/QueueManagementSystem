import { Fragment } from 'react'
import { withStyles } from '@material-ui/core/styles'
import AddManagerRow from '../manager-row/AddManagerRow'
import ExpandMoreIcon from '@material-ui/icons/ExpandMore'
import Grid from '@material-ui/core/Grid'
import ManagerRow from '../manager-row/ManagerRow'
import MuiAccordion from '@material-ui/core/Accordion'
import MuiAccordionDetails from '@material-ui/core/AccordionDetails'
import MuiAccordionSummary from '@material-ui/core/AccordionSummary'
import qrAdapter from '../../adapters/QrAdapter'
import SimpleRatingView from '../rating/SimpleRatingView'
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

const ServiceRow = ({ organizationId, serviceData }) => {
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
                            <object
                                    type="image/svg+xml"
                                    data={qrAdapter.imageSrc(organizationId, serviceData.id)}
                                    width="32px"
                                    height="32px"
                                    style={{objectFit: 'cover'}}>
                                Your browser does not support SVG.
                            </object>
                        </Grid>
                        <Grid item xs={3}>
                            <Typography style={{color: '#a0a0a0', fontSize: '20px', textAlign: 'right'}} variant='body2'>
                                <SimpleRatingView entityId={'service_' + serviceData.id} />
                            </Typography>
                        </Grid>
                    </Grid>
                </AccordionSummary>
                <AccordionDetails>
                    {serviceData.admins && serviceData.admins.map(admin => {                    
                        return <ManagerRow key={admin.id} adminData={admin} />
                    })}
                    <AddManagerRow service={serviceData} />
                </AccordionDetails>
            </Accordion>
        </Fragment>
    )
}

export default ServiceRow
