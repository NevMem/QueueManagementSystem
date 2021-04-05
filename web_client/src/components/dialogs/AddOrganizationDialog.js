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
import addIcon from '../../images/add.svg'

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

const GalleryBlock = ({ setImageUrls, ...props }) => {

    const [ open, setOpen ] = useState(false)
    const [ images, setImages ] = useState([])
    const handleOpen = () => { setOpen(true) }
    const handleClose = () => { setOpen(false) }

    const { value: imageUrl, reset: resetImageUrl, bind: bindImageUrl } = useInput('')

    const addImage = () => {
        const newImages = [...images]
        newImages.push(imageUrl)
        setImages(newImages)
        setImageUrls(newImages)
        resetImageUrl()
        handleClose()
    }

    return (
        <div style={{display: 'flex', flexDirection: 'row', marginTop: '16px'}} {...props}>
            { images.map((elem, index) => {
                return (
                    <img
                        width='48px'
                        height='48px'
                        src={elem}
                        key={elem + ' ' + index}
                        style={{marginRight: '8px'}}
                        alt='some kek' />
                )
            }) }

            <img onClick={handleOpen} src={addIcon} width='48px' height='48px' alt='' />

            <Dialog open={open} onClose={handleClose}>
                <DialogContent>
                    <TextField
                        style={{width: '320px'}}
                        color="primary"
                        variant="outlined"
                        label={localizedString('add_item_to_image_gallery_image_url_label')}
                        {...bindImageUrl} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={addImage}>{localizedString('add_image')}</Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}

export default function AddOrganizationDialog({ open, onClose, ...rest }) {

    const { value: organizationName, bind: bindOrganizationName } = useInput('')
    const { value: organizationAddress, bind: bindOrganizationAddress } = useInput('')
    const { value: description, bind: bindDescription } = useInput('')
    const [ error, setError ] = useState(undefined)
    const [ images, setImages ] = useState([])

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
        orgAdapter.addOrganization(organizationName, organizationAddress, data)
            .then(() => {
                onClose()
            })
            .catch(err => {
                setError(JSON.stringify(err))
            })
    }

    return (
        <Dialog {...rest} open={open} onClose={onClose} aria-labelledby="add-organization-dialog">
            <DialogTitle id="add-organization-dialog">{localizedString('add_new_organization_dialog_title')}</DialogTitle>
            <DialogContent>
                { error && <Typography style={{color: 'red', marginBottom: '16px'}}>{error}</Typography> }
                <TextField
                    style={{width: '100%'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('add_new_organization_dialog_name_label')}
                    {...bindOrganizationName} />

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    label={localizedString('add_new_organization_dialog_address_label')}
                    {...bindOrganizationAddress} />

                <TextField
                    style={{width: '100%', marginTop: '16px'}}
                    color="primary"
                    variant="outlined"
                    size='small'
                    label={localizedString('add_new_organization_dialog_description_label')}
                    {...bindDescription} />

                <GalleryBlock setImageUrls={setImages} />

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
