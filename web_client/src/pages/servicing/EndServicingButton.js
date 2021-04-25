import { actionButtonStyle, dialogContent, formControl } from './styles.js'
import { useState, Fragment } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '../../components/dialogs/StyledDialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import FormControl from '@material-ui/core/FormControl'
import InputLabel from '@material-ui/core/InputLabel'
import localizedString from '../../localization/localizedString'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import servicingAdapter from '../../adapters/ServicingAdapter'

const EndServicingButton = () => {
    const [open, setOpen] = useState(false)

    const handleOpen = () => { setOpen(true) }
    const handleClose = () => { setOpen(false) }

    const [resolution, setResolution] = useState('SERVICED')

    const handleChange = (event) => {
        setResolution(event.target.value)
    }

    const [enabled, setEnabled] = useState(true)

    const handleButtonClick = () => {
        setEnabled(false)
        servicingAdapter.endServicing(resolution)
            .then(() => {
                setEnabled(true)
            })
            .catch(err => {
                console.log(err)
            })
    }

    return (
        <Fragment>
            <Button style={actionButtonStyle} onClick={handleOpen} color='primary' variant='outlined'>
                {localizedString('end_servicing')}
            </Button>
            <Dialog open={open} onClose={handleClose}>
                <DialogContent style={dialogContent}>
                    <FormControl variant="outlined" style={formControl}>
                        <InputLabel id="resolution-label">{localizedString('resolution')}</InputLabel>
                        <Select
                            labelId="resolution-label"
                            value={resolution}
                            onChange={handleChange}
                            disabled={!enabled}
                            label={localizedString('resolution')}>
                            <MenuItem value={'SERVICED'}>{localizedString('SERVICED')}</MenuItem>
                            <MenuItem value={'NOT_SERVICED'}>{localizedString('NOT_SERVICED')}</MenuItem>
                            <MenuItem value={'KICKED'}>{localizedString('KICKED')}</MenuItem>
                        </Select>
                    </FormControl>
                </DialogContent>
                <DialogActions>
                    <Button color='primary' disabled={!enabled} onClick={handleButtonClick}>
                        {localizedString('end_servicing')}
                    </Button>
                </DialogActions>
            </Dialog>
        </Fragment>
    )
}

export default EndServicingButton
