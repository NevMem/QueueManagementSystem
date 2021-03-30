import { Fragment, useState } from 'react'
import AddButton from '../buttons/add_button/AddButton'
import AddManagerDialog from '../dialogs/AddManagerDialog'
import Grid from '@material-ui/core/Grid'
import localizedString from '../../localization/localizedString'

const AddManagerGroup = ({service, ...rest}) => {
    const [ open, setOpen ] = useState()

    const openDialog = () => { setOpen(true) }
    const closeDialog = () => { setOpen(false) }

    return (
        <Fragment>
            <AddButton
                isPrimaryButton={false}
                text={localizedString('new_manager')}
                onClick={openDialog}
                {...rest} />
            <AddManagerDialog service={service} open={open} onClose={closeDialog} />
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
