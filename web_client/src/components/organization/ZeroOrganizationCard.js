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
                        {localizedString('zero_organizations_card')}
                    </Typography>
                </Grid>
            </Grid>
        </div>
    )
}
