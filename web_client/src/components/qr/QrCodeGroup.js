import { Fragment, useState } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogContent from '@material-ui/core/DialogContent'
import localizedString from '../../localization/localizedString'
import qrAdapter from '../../adapters/QrAdapter'

export default function QrCodeGroup({ organizationId, serviceId, ...props }) {
    const [ open, setOpen ] = useState(false)

    const handleOpen = () => { setOpen(true) }
    const handleClose = () => { setOpen(false) }

    return (
        <Fragment>
            <Button onClick={handleOpen} {...props}>{localizedString('show_qr_code')}</Button>
            <Dialog open={open} onClose={handleClose}>
                <DialogContent>
                    <img
                        src={qrAdapter.imageSrc(organizationId, serviceId)}
                        width='512px'
                        height='512px'
                        alt='QR code' />
                </DialogContent>
            </Dialog>
        </Fragment>
    )
}
