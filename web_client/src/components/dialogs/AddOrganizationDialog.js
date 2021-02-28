import { withStyles } from '@material-ui/core/styles'
import Button from '@material-ui/core/Button'
import MuiDialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import TextField from '@material-ui/core/TextField'
import useInput from '../../utils/useInput'
import { green } from '@material-ui/core/colors'

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
            <DialogTitle id="add-organization-dialog">Добавляем новую организацию</DialogTitle>
            <DialogContent>
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label="Имя новой организации"
                    {...bindOrganizationName} />
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
