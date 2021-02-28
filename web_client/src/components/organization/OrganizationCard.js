import './OrganizationCard.css'
import { useState } from 'react'
import AddButton from '../../components/buttons/add_button/AddButton'
import AddServiceDialog from '../dialogs/AddServiceDialog'
import Grid from '@material-ui/core/Grid'
import ServiceRow from '../service-row/ServiceRow'
import Typography from '@material-ui/core/Typography'

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
