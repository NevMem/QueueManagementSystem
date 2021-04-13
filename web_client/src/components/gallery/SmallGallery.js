import { useState } from 'react'
import addIcon from '../../images/add.svg'
import Button from '@material-ui/core/Button'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import localizedString from '../../localization/localizedString'
import StyledDialog from '../dialogs/StyledDialog'
import TextField from '@material-ui/core/TextField'
import useInput from '../../utils/useInput'

export default function SmallGallery({ setImageUrls, nowImages, ...props }) {

    const [ open, setOpen ] = useState(false)
    const [ images, setImages ] = useState(nowImages)
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

            <StyledDialog open={open} onClose={handleClose}>
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
            </StyledDialog>
        </div>
    )
}
