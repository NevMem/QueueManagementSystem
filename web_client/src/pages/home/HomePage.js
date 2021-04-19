import { observer } from 'mobx-react'
import AddButton from '../../components/buttons/add_button/AddButton'
import AddOrganizationDialog from '../../components/dialogs/AddOrganizationDialog'
import CurrentServicingBlock from '../../components/servicing/CurrentServicingBlock'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import localizedString from '../../localization/localizedString'
import orgAdapter from '../../adapters/OrgAdapter'
import OrganizationCard from '../../components/organization/OrganizationCard'
import React, { Component, Fragment } from 'react'
import Typography from '@material-ui/core/Typography'
import ZeroOrganizationCard from '../../components/organization/ZeroOrganizationCard'

const OrganizationCardsBlock = ({ organizationsData, loading }) => {
    return (
        <Fragment>
            { organizationsData.map((elem) => {
                return <OrganizationCard key={elem.id} organizationData={elem} style={{marginTop: '20px'}}  />
            }) }
            { !loading && organizationsData.length === 0 && <ZeroOrganizationCard style={{marginTop: '20px'}} /> }
            {loading && <div style={{color: '#d0d0d0', fontSize: '20px', marginTop: '20px'}}>[TODO] Loading...</div>}
        </Fragment>
    )
}

const WrappedOrganizationCardsBlock = observer(({ orgAdapter }) => {
    return <OrganizationCardsBlock organizationsData={orgAdapter.getOrganizations()} loading={orgAdapter.loading} />
})

export default class HomePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {
            open: false
        }
        this.openDialog = this.openDialog.bind(this)
        this.closeDialog = this.closeDialog.bind(this)
    }

    openDialog() {
        this.setState(state => { return {...state, open: true} })
    }

    closeDialog() {
        this.setState(state => { return {...state, open: false} })
    }
    
    render() {
        return (
            <Fragment>
                <Header />
                <Grid container justify='center'>
                    <Grid item xs={8}>
                        <CurrentServicingBlock />
                        <Grid container justify='space-between' style={{marginTop: '16px'}}>
                            <Typography style={{color: '#a0a0a0', fontSize: '26px'}} variant='body2'>
                                {localizedString('your_organizations')}
                            </Typography>
                            <AddButton
                                isPrimaryButton={true}
                                text={localizedString('new_organization')}
                                onClick={this.openDialog} />
                            <AddOrganizationDialog open={this.state.open} onClose={this.closeDialog} />
                        </Grid>
                        <WrappedOrganizationCardsBlock orgAdapter={orgAdapter} />
                    </Grid>
                </Grid>
            </Fragment>
        )
    }
}
