import './StartServicingBlock.css'
import { observer } from 'mobx-react'
import { Redirect } from 'react-router-dom'
import { useState } from 'react'
import Button from '@material-ui/core/Button'
import localizedString from '../../localization/localizedString'
import servicingAdapter from '../../adapters/ServicingAdapter'
import TextField from '@material-ui/core/TextField'

const AmountToService = observer(({ servicingAdapter }) => {
    return (
        <div className='startAmount'>
            {servicingAdapter.serviceIds.length} {localizedString('services')}
        </div>
    )
})

const WindowName = observer(({ servicingAdapter }) => {

    const handleChange = (event) => {
        servicingAdapter.setWindowName(event.target.value)
    }

    return (
        <TextField
            variant='outlined'
            size='small'
            onChange={handleChange}
            label={localizedString('window_name')}
            value={servicingAdapter.windowName} />
    )
})

const StartButton = observer(({ servicingAdapter, onClick }) => {
    return (
        <Button
            disabled={servicingAdapter.serviceIds.length === 0}
            onClick={onClick}
            color='secondary'
            variant='contained'>
                {localizedString('start')}
        </Button>
    )
})

export default function StartServicingBlock() {
    const [redirect, setRedirectionNeeded] = useState(false)

    const handleClick = () => {
        servicingAdapter.nextUser(servicingAdapter.windowName, servicingAdapter.serviceIds)
            .then(() => {
                setRedirectionNeeded(true)
            })
            .catch(() => {
                setRedirectionNeeded(true)
            })
    }

    return (
        <div className='startServicing' style={{marginTop: '16px'}}>
            { redirect && <Redirect to='/servicing' /> }
            <WindowName servicingAdapter={servicingAdapter} />
            <AmountToService servicingAdapter={servicingAdapter} />
            <StartButton servicingAdapter={servicingAdapter} onClick={handleClick} />
        </div>
    )
}
