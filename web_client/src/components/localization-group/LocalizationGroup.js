import { Fragment, useState } from 'react'
import Button from '@material-ui/core/Button'
import Dialog from '@material-ui/core/Dialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import DialogTitle from '@material-ui/core/DialogTitle'
import localizationService from '../../localization/LocalizationService'
import localizedString from '../../localization/localizedString'
import Typography from '@material-ui/core/Typography'

const LocalizationGroup = ({ ...props }) => {
    const [open, setOpen] = useState(false)
    const openDialog = () => { setOpen(true) }
    const closeDialog = () => { setOpen(false) }
    const setLang = (lang) => {
        localizationService.setCurrentLanguage(lang)
        closeDialog()
    }
    return (
        <Fragment>
            <img
                src={localizationService.currentLanguage().icon}
                onClick={openDialog}
                alt='curren lang icon'
                {...props} />
            <Dialog open={open} onClose={closeDialog} aria-labelledby="change-language-dialog">
                <DialogTitle style={{minWidth: '300px'}} id="change-language-dialog">Изменить язык</DialogTitle>
                <DialogContent>
                    {localizationService.languages().map(elem => {
                        return (
                            <div
                                style={{display: 'flex', flexDirection: 'row', cursor: 'pointer'}}
                                onClick={setLang.bind(this, elem)}
                                key={elem.name}>
                                <img src={elem.icon} style={{height: '30px', padding: '7px'}} alt='lang icon' />
                                <Typography
                                    style={{color: '#a0a0a0', fontSize: '20px', marginLeft: '10px', marginTop: '7px'}}
                                    variant='body2'>
                                    {elem.name}
                                </Typography>
                            </div>
                        )
                    })}
                </DialogContent>
                <DialogActions>
                    <Button autoFocus onClick={closeDialog} color="default">
                        {localizedString('cancel')}
                    </Button>
                </DialogActions>
            </Dialog>
        </Fragment>
    )
}

export default LocalizationGroup
