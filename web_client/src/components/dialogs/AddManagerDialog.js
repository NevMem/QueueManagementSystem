import { green } from '@material-ui/core/colors'
import { useState } from 'react'
import { withStyles } from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import localizedString from '../../localization/localizedString'
import MuiDialog from '@material-ui/core/Dialog'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'
import useInput from '../../utils/useInput'
import orgAdapter from '../../adapters/OrgAdapter'

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

export default function AddManagerDialog({ service, open, onClose, ...rest }) {

    const [ error, setError ] = useState(undefined)
    const { value: managerEmail, bind: bindManagerEmail } = useInput('')

    const handleCancel = () => { onClose() }
    const handleOk = () => {
        orgAdapter.addManagerToService(service.id, managerEmail)
            .then(() => {})
            .catch(err => {
                setError(JSON.stringify(err))
            })
    }

    return (
        <Dialog {...rest} open={open} onClose={onClose} aria-labelledby="add-manager-dialog">
            <DialogTitle id="add-manager-dialog">
                {localizedString('add_new_manager_dialog_title')}
            </DialogTitle>
            <DialogContent style={{minWidth: '350px'}}>
                { error && <Typography style={{color: 'red', marginBottom: '16px'}}>{error}</Typography> }
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('add_new_manager_dialog_email_label')}
                    {...bindManagerEmail} />
            </DialogContent>
            <DialogActions>
                <Button autoFocus onClick={handleCancel} color="default">
                    {localizedString('cancel')}
                </Button>
                <Button onClick={handleOk} variant="contained" color="primary">
                    {localizedString('ok')}
                </Button>
            </DialogActions>
        </Dialog>
    )
}
