import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import localizedString from '../../localization/localizedString'

const ManagerRow = ({ adminData }) => {
    const { name, surname, permissionType } = adminData
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}} justify='space-between'>
            <Grid item>
                <Typography style={{color: '#a0a0a0', fontSize: '20px'}} variant='body2'>
                    {name} {surname}
                </Typography>
            </Grid>
            <Grid item>
                <Typography style={{color: '#a0a0a0', fontSize: '16px', textAlign: 'right'}} variant='body2'>
                    {localizedString('permission_' + permissionType)}
                </Typography>
            </Grid>
        </Grid>
    )
}

export default ManagerRow
