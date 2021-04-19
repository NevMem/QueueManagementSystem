import './CurrentServicingBlock.css'
import { Link } from 'react-router-dom'
import { observer } from 'mobx-react'
import Button from '@material-ui/core/Button'
import localizedString from '../../localization/localizedString'
import servicingAdapter from '../../adapters/ServicingAdapter'

const ServicingBlockImpl = observer(({ adapter }) => {
    const current_ticket = adapter.current_ticket
    if (current_ticket === undefined) {
        return null
    }
    return (
        <div className='servicingBlock'>
            <div style={{display: 'flex', flexDirection: 'row', alignItems: 'center'}}>
                <div className='servicingPulse' />
                <div style={{marginLeft: '12px'}}>{localizedString('continue_servicing')}</div>
            </div>
            <Link to='/servicing' style={{textDecoration: 'none'}}>
                <Button variant='text' color='primary'>{localizedString('continue')}</Button>
            </Link>
        </div>
    )
})

export default function CurrentServicingBlock() {
    return <ServicingBlockImpl adapter={servicingAdapter} />
}
