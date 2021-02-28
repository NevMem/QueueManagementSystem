import { Fragment, useState } from 'react'
import AddButton from '../../components/buttons/add_button/AddButton'
import AddQueueDialog from '../dialogs/AddQueueDialog'
import Grid from '@material-ui/core/Grid'

const AddQueueGroup = ({service, ...rest}) => {
    const [open, setOpen] = useState(false)
    const handleOpen = () => { setOpen(true) }
    const handleClose = () => { setOpen(false) }
    return (
        <Fragment>
            <AddButton onClick={handleOpen} isPrimaryButton={false} text='Новая очередь' {...rest} />
            <AddQueueDialog open={open} onClose={handleClose} service={service} />
        </Fragment>
    )
}

const AddQueueRow = ({service, ...rest}) => {
    return (
        <Grid container style={{paddingLeft: '16px', marginTop: '16px'}} justify='center' {...rest}>
            <AddQueueGroup service={service} style={{display: 'inline-block'}} />
        </Grid>
    )
}

export default AddQueueRow
