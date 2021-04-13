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

export default function EditServiceDialog({ service, organizationId, open, onClose, ...rest }) {

    const parseDescription = () => {
        if (service.data === undefined) {
            return ''
        }
        return service.data['description']
    }

    const parseChecklist = () => {
        if (service.data === undefined || service.data['checklistItems'] === undefined) {
            return []
        }
        return service.data['checklistItems'].split(',')
    }

    const checklist = useState(new Checklist())[0]
    const [useChecklist, setUseChecklist] = useState(parseChecklist().length > 0)
    const enableChecklist = () => { setUseChecklist(true) }

    checklist.resetItemsWith(parseChecklist())

    const { value: serviceName, bind: bindServiceName } = useInput(service.name)
    const { value: description, bind: bindDescription } = useInput(parseDescription)
    const [ error, setError ] = useState('')

    const handleCancel = () => { onClose() }

    const handleOk = () => {
        let data = {
            checklistItems: checklist.getList().map(elem => elem.name).join(','),
            description: description
        }
        orgAdapter.updateService(service.id, organizationId, serviceName, data)
            .then(() => {
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
                aria-labelledby={"edit-service-dialog-" + organizationId}>
            <DialogTitle id={"edit-service-dialog" + organizationId} style={{minWidth: '400px'}}>
                {localizedString('edit_service_dialog_title')}
            </DialogTitle>
            <DialogContent>
                {error && <Typography style={{color: 'red', marginBottom: '16px'}}>{error}</Typography> }
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('edit_service_dialog_label')}
                    {...bindServiceName} />

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    size='small'
                    label={localizedString('edit_service_dialog_description_label')}
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
