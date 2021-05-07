import { observer } from 'mobx-react'
import localizedString from '../../localization/localizedString'

const ServicingInfoBlock = observer(({ servicingAdapter }) => {
    return (
        <div style={{display: 'flex', flexDirection: 'column', color: '#d0d0d0', fontSize: '20px'}}>
            <div style={{display: 'flex', flexDirection: 'row'}}>
                <div style={{color: '#a0a0a0'}}>{localizedString('window_name')}</div>
                <div style={{marginLeft: '10px'}}>{servicingAdapter.windowName}</div>
            </div>
            <div style={{display: 'flex', flexDirection: 'row'}}>
                <div style={{color: '#a0a0a0'}}>{localizedString('waiting')}</div>
                <div style={{marginLeft: '10px'}}>{servicingAdapter.filteredTickets.length}</div>
            </div>
        </div>
    )
})

export default ServicingInfoBlock
