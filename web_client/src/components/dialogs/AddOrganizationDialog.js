import { green } from '@material-ui/core/colors'
import { withStyles } from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import localizedString from '../../localization/localizedString'
import MuiDialog from '@material-ui/core/Dialog'
import TextField from '@material-ui/core/TextField'
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

export default function AddOrganizationDialog({ open, onClose, ...rest }) {

    const { value: organizationName, bind: bindOrganizationName } = useInput('')

    const handleCancel = () => {
        onClose()
    }

    const handleOk = () => {
        onClose()
    }

    return (
        <Dialog {...rest} open={open} onClose={onClose} aria-labelledby="add-organization-dialog">
            <DialogTitle id="add-organization-dialog">{localizedString('add_new_organization_dialog_title')}</DialogTitle>
            <DialogContent>
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('add_new_organization_dialog_label')}
                    {...bindOrganizationName} />
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
