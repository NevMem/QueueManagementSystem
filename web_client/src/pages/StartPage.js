import './StartPage.css'
import { makeStyles } from '@material-ui/core/styles'
import { React, Component } from 'react'
import Button from '@material-ui/core/Button'
import Card from '@material-ui/core/Card'
import CardContent from '@material-ui/core/CardContent'
import Grid from '@material-ui/core/Grid'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'

const useStyles = makeStyles({
    authCard: {
        minWidth: '350px',
        margin: 'auto 64px',
        boxShadow: '0px 3px 6px rgb(240, 240, 240)',
        padding: '16px',
    },
    buttons: {
        marginTop: '48px',
        marginRight: '16px',
    }
})

function LoginSection() {
    const classes = useStyles()
    return (
        <div style={{height: '100vh', display: 'flex', justifyContent: 'center'}} className="login_section">
            <Card variant="outlined" className={classes.authCard}>
                <CardContent>
                    <Typography variant="h4">Login or register to moderate your queues</Typography>
                    <TextField style={{width: '100%', marginTop: '48px'}} variant="outlined" label="Login" />
                    <TextField style={{width: '100%', marginTop: '16px'}} variant="outlined" label="Password" />
                    <Button className={classes.buttons} size="large" variant="contained" color="primary">Login</Button>
                    <Button className={classes.buttons} size="large" variant="outlined" color="secondary">Register</Button>
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
                    <div style={{height: '100vh'}} className="intro_section">
                        
                    </div>
                </Grid>
                <Grid item xs={12} md={5}>
                    <LoginSection />
                </Grid>
            </Grid>
        )
    }
}
