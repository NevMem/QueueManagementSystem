import './StartPage.css'
import { React, Component, useState } from 'react'
import Grid from '@material-ui/core/Grid'
import localizedString from '../../localization/localizedString'
import logo from '../../images/connection.svg'
import Typography from '@material-ui/core/Typography'
import LoginSection from './LoginSection'

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
