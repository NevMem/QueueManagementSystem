import './ServicingPage.css'
import { Fragment } from 'react'
import { observer } from 'mobx-react'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import localizedString from '../../localization/localizedString'
import servicingAdapter from '../../adapters/ServicingAdapter'
import SimpleRatingView from '../../components/rating/SimpleRatingView'
import Button from '@material-ui/core/Button'


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

const JustNextActionBlock = () => {
  return (
    <div className='actionsBlock'>
      <Button style={actionButtonStyle} color='primary' variant='outlined'>
        {localizedString('service_next')}
      </Button>
    </div>
  )
}

const AllActionsBlock = () => {
  return (
    <div className='actionsBlock'>
      <Button style={actionButtonStyle} color='primary' variant='outlined'>
        {localizedString('end_servicing')}
      </Button>
      <Button style={actionButtonStyle} color='primary' variant='text'>
        {localizedString('service_next')}
      </Button>
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
