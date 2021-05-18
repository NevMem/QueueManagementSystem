import { useState } from 'react'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import localizedString from '../../localization/localizedString'
import orgAdapter from '../../adapters/OrgAdapter'
import SmallGallery from '../gallery/SmallGallery'
import StyledDialog from '../dialogs/StyledDialog'
import TextField from '@material-ui/core/TextField'
import Typography from '@material-ui/core/Typography'
import useInput from '../../utils/useInput'

export default function EditOrganizationDialog({ organization, open, onClose, ...rest }) {

    const parseImages = () => {
        if (organization.data === undefined) {
            return []
        }
        const imageCount = organization.data['image_count'] | 0
        if (imageCount === undefined || imageCount === 0) {
            return []
        }
        const result = []
        for (let i = 0; i !== imageCount; ++i) {
            result.push(organization.data['image_' + i])
        }
        return result
    }

    const { value: organizationName, bind: bindOrganizationName } = useInput(organization.name)
    const { value: organizationAddress, bind: bindOrganizationAddress } = useInput(organization.address)
    const { value: description, bind: bindDescription } = useInput(organization.data['description'])
    const [ error, setError ] = useState(undefined)
    const [ images, setImages ] = useState(parseImages())

    const handleCancel = () => {
        onClose()
    }

    const handleOk = () => {
        const data = {
            description: description
        }
        data['image_count'] = images.length + ''
        for (let i = 0; i !== images.length; ++i) {
            data['image_' + i] = images[i]
        }
        orgAdapter.updateOrganization(
            organization.id,
            organizationName,
            organizationAddress,
            data,
            organization.timetable)
            .then(() => {
                onClose()
            })
            .catch(err => {
                setError(JSON.stringify(err))
            })
    }

    return (
        <StyledDialog {...rest} open={open} onClose={onClose} aria-labelledby="add-organization-dialog">
            <DialogTitle id="add-organization-dialog">
                {localizedString('edit_organization_dialog_title')}
            </DialogTitle>
            <DialogContent>
                { error && <Typography style={{color: 'red', marginBottom: '16px'}}>{error}</Typography> }
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('organization_dialog_name_label')}
                    {...bindOrganizationName} />

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('organization_dialog_address_label')}
                    {...bindOrganizationAddress} />

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    size='small'
                    label={localizedString('organization_dialog_description_label')}
                    {...bindDescription} />

                <SmallGallery setImageUrls={setImages} nowImages={images} />

            </DialogContent>
            <DialogActions>
                <Button autoFocus onClick={handleCancel} color="default">
                    {localizedString('cancel')}
                </Button>
                <Button onClick={handleOk} variant="contained" color="primary">
                    {localizedString('ok')}
                </Button>
            </DialogActions>
        </StyledDialog>
    )
}
