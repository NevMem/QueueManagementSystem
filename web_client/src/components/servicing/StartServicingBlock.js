import './StartServicingBlock.css'
import { observer } from 'mobx-react'
import Button from '@material-ui/core/Button'
import localizedString from '../../localization/localizedString'
import servicingAdapter from '../../adapters/ServicingAdapter'

const AmountToService = observer(({ servicingAdapter }) => {
    return (
        <div className='startAmount'>
            {servicingAdapter.serviceIds.length} {localizedString('services')}
        </div>
    )
})

export default function StartServicingBlock() {
    return (
        <div className='startServicing' style={{marginTop: '16px'}}>
            <AmountToService servicingAdapter={servicingAdapter} />
            <Button color='secondary' variant='contained'>{localizedString('start')}</Button>
        </div>
    )
}
