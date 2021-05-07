import './ServicingPage.css'
import { Fragment, } from 'react'
import { observer } from 'mobx-react'
import EndServicingButton from './EndServicingButton'
import Grid from '@material-ui/core/Grid'
import Header from '../../components/header/Header'
import ServiceNextButton from './ServiceNextButton'
import servicingAdapter from '../../adapters/ServicingAdapter'
import UserBlock from './CurrentUserBlock'
import ServicingInfoBlock from './ServicingInfoBlock'

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
            <ServicingInfoBlock servicingAdapter={servicingAdapter} />
          </div>
          <div className='servicingCard'>
            <UserBlock servicingAdapter={servicingAdapter} />

            <ActionsBlock servicingAdapter={servicingAdapter} />
          </div>
        </Grid>
      </Grid>
    </Fragment>
  )
}
