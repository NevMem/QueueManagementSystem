import { Fragment } from 'react'
import authAdapter from '../../adapters/AuthAdapter'
import Button from '@material-ui/core/Button'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import localizedString from '../../localization/localizedString'
import Typography from '@material-ui/core/Typography'

export default function SettingsPage() {

    const logout = () => {
        authAdapter.logout()
    }

    return (
        <Fragment>
            <Header />
            <Grid container justify='center'>
                <Grid item xs={8}>
                    <Grid justify='space-between' style={{marginTop: '16px', display: 'flex', flexDirection: 'row'}}>
                        <Typography variant='h6' style={{color: '#a0a0a0'}}>{localizedString('logout')}</Typography>
                        <Button color='secondary' variant='contained' onClick={logout}>
                            {localizedString('logout_button')}
                        </Button>
                    </Grid>
                </Grid>
            </Grid>
        </Fragment>
    )
}
