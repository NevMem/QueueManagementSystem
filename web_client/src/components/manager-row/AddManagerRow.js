import { Fragment } from 'react'
import AddButton from '../buttons/add_button/AddButton'
import Grid from '@material-ui/core/Grid'
import localizedString from '../../localization/localizedString'

const AddManagerGroup = ({service, ...rest}) => {
    return (
        <Fragment>
            <AddButton isPrimaryButton={false} text={localizedString('new_manager')} {...rest} />
        </Fragment>
    )
}

const AddManagerRow = ({service, ...rest}) => {
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}} justify='center' {...rest}>
            <AddManagerGroup service={service} style={{display: 'inline-block'}} />
        </Grid>
    )
}

export default AddManagerRow
