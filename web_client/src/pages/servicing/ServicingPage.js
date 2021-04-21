import './ServicingPage.css'
import { Fragment, useState } from 'react'
import { observer } from 'mobx-react'
import Button from '@material-ui/core/Button'
import Dialog from '../../components/dialogs/StyledDialog'
import DialogActions from '@material-ui/core/DialogActions'
import DialogContent from '@material-ui/core/DialogContent'
import FormControl from '@material-ui/core/FormControl'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import InputLabel from '@material-ui/core/InputLabel'
import localizedString from '../../localization/localizedString'
import MenuItem from '@material-ui/core/MenuItem'
import Select from '@material-ui/core/Select'
import servicingAdapter from '../../adapters/ServicingAdapter'
import SimpleRatingView from '../../components/rating/SimpleRatingView'


const UserBlock = observer(({ servicingAdapter }) => {
  const currentTicket = servicingAdapter.currentTicket

  if (currentTicket === undefined) {
    return null
  }

  const user = currentTicket.user

  return (
    <Fragment>
      <p className='clientHeader'>{localizedString('client')}</p>

      <div className='userInfoRow'>
        <div className='description'>{localizedString('email')}</div>
        <div>{user.email}</div>
      </div>

      <div className='userInfoRow'>
        <div className='description'>{localizedString('name')}</div>
        <div>{user.name}</div>
      </div>

      <div className='userInfoRow'>
        <div className='description'>{localizedString('surname')}</div>
        <div>{user.surname}</div>
      </div>
      
      <div className='userInfoRow'>
        <div className='description'>{localizedString('rating')}</div>
        <div><SimpleRatingView entityId={'user_' + user.email} /></div>
      </div>
    </Fragment>
  )
})

const actionButtonStyle = {
  marginLeft: '5px',
  marginRight: '5px',
}

const ServiceNextButton = () => {
  const handleClick = () => {
    servicingAdapter.nextUser()
  }

  return (
    <Button style={actionButtonStyle} onClick={handleClick} color='secondary' variant='text'>
      {localizedString('service_next')}
    </Button>
  )
}

const EndServicingButton = () => {
  const [open, setOpen] = useState(false)

  const handleOpen = () => { setOpen(true) }
  const handleClose = () => { setOpen(false) }

  const [resolution, setResolution] = useState('SERVICED')

  const handleChange = (event) => {
    setResolution(event.target.value)
  }

  const [enabled, setEnabled] = useState(true)

  const handleButtonClick = () => {
    setEnabled(false)
    servicingAdapter.endServicing(resolution)
      .then(() => {
        setEnabled(true)
      })
      .catch(err => {
        console.log(err)
      })
  }

  return (
    <Fragment>
      <Button style={actionButtonStyle} onClick={handleOpen} color='primary' variant='outlined'>
        {localizedString('end_servicing')}
      </Button>
      <Dialog open={open} onClose={handleClose}>
        <DialogContent style={{minWidth: '300px'}}>
          <FormControl variant="outlined" style={{width: '100%'}}>
            <InputLabel id="resolution-label">{localizedString('resolution')}</InputLabel>
            <Select
              labelId="resolution-label"
              value={resolution}
              onChange={handleChange}
              disabled={!enabled}
              label={localizedString('resolution')}
            >
              <MenuItem value={'SERVICED'}>{localizedString('SERVICED')}</MenuItem>
              <MenuItem value={'NOT_SERVICED'}>{localizedString('NOT_SERVICED')}</MenuItem>
              <MenuItem value={'KICKED'}>{localizedString('KICKED')}</MenuItem>
            </Select>
          </FormControl>
        </DialogContent>
        <DialogActions>
          <Button color='primary' disabled={!enabled} onClick={handleButtonClick}>
            {localizedString('end_servicing')}
          </Button>
        </DialogActions>
      </Dialog>
    </Fragment>
  )
}

const JustNextActionBlock = () => {
  return (
    <div className='actionsBlock'>
      <ServiceNextButton />
    </div>
  )
}

const AllActionsBlock = () => {
  return (
    <div className='actionsBlock'>
      <EndServicingButton />
      <ServiceNextButton />
    </div>
  )
}

const ActionsBlock = observer(({ servicingAdapter }) => {
  const currentTicket = servicingAdapter.currentTicket

  if (currentTicket === undefined) {
    return <JustNextActionBlock />
  }

  return <AllActionsBlock />
})

export default function ServicingPage() {
  return (
    <Fragment>
      <Header />
      <Grid container justify='center'>
        <Grid item xs={8}>
          <div className='servicingCard'>
            <UserBlock servicingAdapter={servicingAdapter} />

            <ActionsBlock servicingAdapter={servicingAdapter} />
          </div>
        </Grid>
      </Grid>
    </Fragment>
  )
}
