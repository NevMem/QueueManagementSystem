import { green } from '@material-ui/core/colors'
import { useState } from 'react'
import { withStyles } from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import localizedString from '../../localization/localizedString'
import MuiDialog from '@material-ui/core/Dialog'
import orgAdapter from '../../adapters/OrgAdapter'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'
import useInput from '../../utils/useInput'

const Dialog = withStyles({
    root: {
        '& label.Mui-focused': {
            color: green.A700,
        },
        '& .MuiInput-underline:after': {
            borderBottomColor: green.A700,
        },
        '& .MuiOutlinedInput-root': {
            '& fieldset': {
            },
            '&:hover fieldset': {
            },
            '&.Mui-focused fieldset': {
                borderColor: green.A700,
            },
        },
    }
})(MuiDialog)

export default function AddServiceDialog({ organization, open, onClose, ...rest }) {

    const { value: serviceName, bind: bindServiceName } = useInput('')
    const [ error, setError ] = useState('')

    const handleCancel = () => { onClose() }

    const handleOk = () => {
        orgAdapter.addService(organization.id, serviceName)
            .then(data => {
                onClose()
            })
            .catch(err => {
                if (err.message) {
                    setError(err.message)
                } else {
                    setError(JSON.stringify(err))
                }
            })
    }

    return (
        <Dialog
                {...rest}
                open={open}
                onClose={onClose}
                aria-labelledby={"add-service-dialog-" + organization.id}>
            <DialogTitle id={"add-service-dialog" + organization.id} style={{minWidth: '400px'}}>
                {localizedString('add_new_service_dialog_title')}
            </DialogTitle>
            <DialogContent>
                {error && <Typography style={{color: 'red', marginBottom: '16px'}}>{error}</Typography> }
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('add_new_service_dialog_label')}
                    {...bindServiceName} />
            </DialogContent>
            <DialogActions>
                <Button autoFocus onClick={handleCancel} color="default">
                    Cancel
                </Button>
                <Button onClick={handleOk} variant="contained" color="primary">
                    Ok
                </Button>
            </DialogActions>
        </Dialog>
    )
}
