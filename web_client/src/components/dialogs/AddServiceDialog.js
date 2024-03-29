import { useState } from 'react'
import AddIcon from '@material-ui/icons/Add'
import Button from '@material-ui/core/Button'
import Checklist from '../../data/checklist'
import ChecklistBlock from '../checklist/ChecklistBlock'
import Dialog from '../dialogs/StyledDialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import Grid from '@material-ui/core/Grid'
import IconButton from '@material-ui/core/IconButton'
import localizedString from '../../localization/localizedString'
import orgAdapter from '../../adapters/OrgAdapter'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'
import useInput from '../../utils/useInput'

export default function AddServiceDialog({ organization, open, onClose, ...rest }) {

    const { value: serviceName, bind: bindServiceName } = useInput('')
    const { value: description, bind: bindDescription } = useInput('')
    const [ error, setError ] = useState('')

    const handleCancel = () => { onClose() }

    const checklist = new Checklist()

    const handleOk = () => {
        let data = {
            checklistItems: checklist.getList().map(elem => elem.name).join(','),
            description: description
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

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    size='small'
                    label={localizedString('add_new_service_dialog_description_label')}
                    {...bindDescription} />

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
