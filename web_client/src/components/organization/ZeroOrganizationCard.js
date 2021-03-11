import './OrganizationCard.css'
import Grid from '@material-ui/core/Grid'
import localizedString from '../../localization/localizedString'
import Typography from '@material-ui/core/Typography'

export default function ZeroOrganizationCard({ ...props }) {
    return (
        <div className = 'organizationCard' {...props}>
            <Grid container justify='space-around'>
                <Grid item>
                    <Typography style={{color: '#c0c0c0', fontSize: '24px'}} variant='body2'>
                        {localizedString('zero_organizations_card_title')}
                    </Typography>
                </Grid>
                <Grid item style={{marginTop: '16px'}}>
                    <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>
                        {localizedString('zero_organizations_card_desc')}
                    </Typography>
                </Grid>
            </Grid>
        </div>
    )
}
