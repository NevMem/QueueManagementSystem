import React, { Component, Fragment } from 'react'
import Header from '../../components/header/Header'
import Grid from '@material-ui/core/Grid'
import Typography from '@material-ui/core/Typography'
import AddButton from '../../components/buttons/add_button/AddButton'

export default class HomePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {}
    }
    
    render() {
        return (
            <Fragment>
                <Header />
                <Grid container justify='center'>
                    <Grid item xs={10}>
                        <Grid container justify='space-evenly' style={{marginTop: '16px'}}>
                            <Typography style={{color: '#a0a0a0', fontSize: '26px'}} variant='body2'>
                                Ваши организации
                            </Typography>
                            <AddButton isPrimaryButton={true} text='Новая организация' />
                        </Grid>
                    </Grid>
                </Grid>
            </Fragment>
        )
    }
}
