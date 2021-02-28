import './OrganizationCard.css'
import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import AddButton from '../../components/buttons/add_button/AddButton'
import Accordion from '@material-ui/core/Accordion'
import AccordionDetails from '@material-ui/core/AccordionDetails'
import AccordionSummary from '@material-ui/core/AccordionSummary'

const QueueRow = ({ queueData }) => {
    return null
}

const ServiceRow = ({ serviceData }) => {
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}}>
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