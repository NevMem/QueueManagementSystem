import './StartPage.css'
import { makeStyles } from '@material-ui/core/styles'
import { React, Component, useState } from 'react'
import { Redirect } from 'react-router-dom'
import authAdapter from '../adapters/AuthAdapter'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import Grid from '@material-ui/core/Grid'
import LocalizationGroup from '../components/localization-group/LocalizationGroup'
import localizedString from '../localization/localizedString'
import logo from '../images/connection.svg'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'
import useInput from '../utils/useInput'

const useStyles = makeStyles({
    authCard: {
        minWidth: '350px',
        margin: 'auto 64px',
        padding: '16px',
        backgroundColor: 'rgba(0, 0, 0, 0)',
        borderWidth: '2px',
    },
    buttons: {
        marginRight: '16px',
    }
})

function LoginSection() {
    const classes = useStyles()

    const { value: login, bind: bindLogin, reset: resetLogin } = useInput('')
    const { value: password, bind: bindPassword, reset: resetPassword } = useInput('')
    const [ error, setError ] = useState(undefined)
    const [ redirect, initRedirection ] = useState(undefined)

    const handleSubmit = () => {
        authAdapter.login(login, password)
            .then(() => {
                resetLogin()
                resetPassword()
                initRedirection('/')
            })
            .catch(err => {
                setError(err)
            })
    }

    return (
        <div style={{height: '100vh', display: 'flex', justifyContent: 'center'}} className="login_section">
            <Card variant="outlined" className={classes.authCard}>
                <CardContent>
                    <Typography variant="h4">{localizedString('login_form_title')}</Typography>
                    { error && <Typography variant="h6">{error}</Typography> }
                    { redirect && <Redirect to={redirect} /> }
                    <TextField
                        style={{width: '100%', marginTop: '48px'}}
                        variant="outlined"
                        label={localizedString('login_label')}
                        {...bindLogin} />
                    <TextField
                        style={{width: '100%', marginTop: '16px'}}
                        variant="outlined"
                        label={localizedString('password_label')}
                        {...bindPassword} />
                    <div style={{display: 'flex', flexDirection: 'row', marginTop: '48px', justifyContent: 'left'}}>
                        <Button className={classes.buttons} size="large" variant="contained" color="primary" onClick={handleSubmit}>
                            {localizedString('login')}
                        </Button>
                        <Button className={classes.buttons} size="large" variant="outlined" color="secondary">
                            {localizedString('register')}
                        </Button>
                        <LocalizationGroup style={{height: '30px', padding: '7px', cursor: 'pointer'}} />
                    </div>
                </CardContent>
            </Card>
        </div>
    )
}

export default class StartPage extends Component {
    render() {
        return (
            <Grid container>
                <Grid item xs={12} md={7}>
                    <div style={{height: '100vh', display: 'flex', justifyContent: 'center'}} className="intro_section">
                        <div style={{margin: 'auto auto', display: 'flex', justifyContent: 'center', flexDirection: 'column'}}>
                            <img src={logo} alt="logo" style={{margin: '0 auto'}} className="logo"/>
                            <Typography variant="h4" style={{maxWidth: '500px', textAlign: 'center', color: '#ccc'}}>
                                {localizedString('app_intro_text')}
                            </Typography>
                        </div>
                    </div>
                </Grid>
                <Grid item xs={12} md={5}>
                    <LoginSection />
                </Grid>
            </Grid>
        )
    }
}
