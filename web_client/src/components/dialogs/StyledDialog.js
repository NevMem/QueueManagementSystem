import MuiDialog from '@material-ui/core/Dialog'
import { green } from '@material-ui/core/colors'
import { withStyles } from '@material-ui/core/styles'

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

export default Dialog
