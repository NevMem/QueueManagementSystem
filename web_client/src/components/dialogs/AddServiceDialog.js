import { Fragment, useState } from 'react'
import { green } from '@material-ui/core/colors'
import { makeAutoObservable } from 'mobx'
import { observer } from 'mobx-react'
import { withStyles } from '@material-ui/core/styles'
import AddIcon from '@material-ui/icons/Add'
import Button from '@material-ui/core/Button'
import DeleteIcon from '@material-ui/icons/Delete'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import InputAdornment from '@material-ui/core/InputAdornment'
import localizedString from '../../localization/localizedString'
import MuiDialog from '@material-ui/core/Dialog'
import orgAdapter from '../../adapters/OrgAdapter'
import OutlinedInput from '@material-ui/core/OutlinedInput'
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

class Checklist {
    constructor() {
        this.list = []
        this.id = 0
        makeAutoObservable(this)
    }

    getList() {
        return this.list
    }

    addItem(title) {
        const newList = [...this.list]
        newList.push({
            id: this.id,
            name: title
        })
        this.list = newList
        this.id += 1
    }

    removeItem(item) {
        const index = this.list.indexOf(item)
        if (index < 0) {
            return
        }
        this.list.splice(index, 1)
    }
}

const ChecklistBlock = observer(({ checklist }) => {

    const [value, setValue] = useState('')
    const handleChange = (ev) => {
        setValue(ev.target.value)
    }
    const addItem = () => {
        checklist.addItem(value)
        setValue('')
    }
    const handleDelete = (item) => {
        checklist.removeItem(item)
    }

    return (
        <Fragment>
            <Typography
                style={{marginTop: '12px', marginBottom: '12px'}}>
                    {localizedString('checklist_header')}
            </Typography>
            { checklist.getList().map((elem) => {
                return (
                    <Grid container key={elem.id} justify='space-between'>
                        <Typography style={{lineHeight: '48px'}}>{elem.name}</Typography>
                        <IconButton
                            aria-label='delete checklist item'
                            onClick={handleDelete.bind(elem, elem)}>
                                <DeleteIcon />
                        </IconButton>
                    </Grid>
                )
            }) }
            <Grid container>
                <OutlinedInput
                    margin='dense'
                    value={value}
                    style={{width: '100%'}}
                    endAdornment={
                        <InputAdornment position='end'>
                            <IconButton
                                aria-label="add item to checklist"
                                onClick={addItem}
                                edge="end">
                                <AddIcon />
                            </IconButton>
                        </InputAdornment>
                    }
                    onChange={handleChange} />
            </Grid>
        </Fragment>
    )
})

export default function AddServiceDialog({ organization, open, onClose, ...rest }) {

    const { value: serviceName, bind: bindServiceName } = useInput('')
    const [ error, setError ] = useState('')

    const handleCancel = () => { onClose() }

    const checklist = new Checklist()

    const handleOk = () => {
        let data = {
            checklistItems: checklist.getList().map(elem => elem.name).join(',')
        }
        orgAdapter.addService(organization.id, serviceName, data)
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

    const [useChecklist, setUseChecklist] = useState(false)
    const enableChecklist = () => { setUseChecklist(true) }

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

                { useChecklist && <ChecklistBlock checklist={checklist} /> }
                { !useChecklist &&
                    <Grid container justify='space-between' style={{flexDirection: 'row', flexWrap: 'nowrap'}}>
                        <Typography style={{marginTop: '12px'}}>{localizedString('about_checklist')}</Typography>
                        <IconButton aria-label='add checklist' onClick={enableChecklist}>
                            <AddIcon />
                        </IconButton>
                    </Grid>
                }

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
