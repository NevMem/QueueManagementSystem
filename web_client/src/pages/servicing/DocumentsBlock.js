import './DocumentBlock.css'
import { observer } from 'mobx-react'
import { useState } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '../../components/dialogs/StyledDialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import extractDocuments from '../../parsers/documents'
import localizedString from '../../localization/localizedString'

const Document = ({ document }) => {

    const [open, setOpen] = useState(false)

    const handleClick = () => {
        setOpen(true)
    }

    const handleClose = () => {
        setOpen(false)
    }

    return (
        <div className='document'>
            <div onClick={handleClick}>{localizedString('document_type_' + document.type)}</div>
            <Dialog open={open} onClose={handleClose}>
                <DialogContent>
                    <div style={{margin: '8px 0px', fontSize: '20px'}}>
                        {localizedString('document_type_' + document.type)}
                    </div>
                    { document.series && (
                        <div style={{display: 'flex', flexDirection: 'row', margin: '8px 0px'}}>
                            <div>{localizedString('series')}:</div>
                            <div style={{marginLeft: '8px'}}>{document.series}</div>
                        </div>
                    ) }
                    <div style={{display: 'flex', flexDirection: 'row', margin: '8px 0px'}}>
                        <div>{localizedString('number')}:</div>
                        <div style={{marginLeft: '8px'}}>{document.number}</div>
                    </div>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose}>{localizedString('ok')}</Button>
                </DialogActions>
            </Dialog>
        </div>
    )
}

const DocumentsBlock = observer(({ servicingAdapter }) => {

    if (servicingAdapter.currentTicket === undefined) {
        return null
    }

    const documents = extractDocuments(servicingAdapter.currentTicket.user)

    return (
        <div style={{display: 'flex', flexDirection: 'row', flexWrap: 'wrap'}}>
            { documents.map((elem, index) => {
                return (
                    <Document key={index} document={elem} />
                )
            }) }
        </div>
    )
})

export default DocumentsBlock
