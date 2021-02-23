import './OrganizationCard.css'
import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import AddButton from '../../components/buttons/add_button/AddButton'


export default function OrganizationCard({...props}) {
    return (
        <div className = 'organizationCard' {...props}>
            <Grid container justify='space-between'>
                <Grid item xs={4}>
                    <Typography style={{color: '#c0c0c0', fontSize: '30px'}} variant='body2'>
                        Аптека на тверской
                    </Typography>
                </Grid>
                <Grid item xs={2}>
                    <AddButton isPrimaryButton={false} text='Новый сервис' />
                </Grid>
            </Grid>
            <Grid container style={{marginTop: '20px', marginBottom: '20px'}}>
                <Grid item>
                    <Typography style={{color: '#a0a0a0', fontSize: '24px'}} variant='body2'>
                        Сервисы:
                    </Typography>
                </Grid>
            </Grid>
            <Grid container style={{paddingLeft: '16px'}}>
                <Grid item xs={2}>
                    <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>Кофе</Typography>
                </Grid>
                <Grid item xs={4}>
                    <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>10 клиентов ожидают</Typography>
                </Grid>
                <Grid item xs={3}>
                    <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>~1:30 мин/чел</Typography>
                </Grid>
                <Grid item xs={3}>
                    <Typography style={{color: '#a0a0a0', fontSize: '20px', textAlign: 'right'}} variant='body2'>4.0</Typography>
                </Grid>
            </Grid>
        </div>
    )
}