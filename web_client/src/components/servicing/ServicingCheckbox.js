import Checkbox from '@material-ui/core/Checkbox'
import { observer } from 'mobx-react'
import servicingAdapter from '../../adapters/ServicingAdapter'

const ActualCheckbox = observer(({ servicingAdapter, serviceId }) => {
    const index = servicingAdapter.serviceIds.indexOf(serviceId)

    const handleChange = (event) => {
        if (event.target.checked) {
            servicingAdapter.addServiceToServicing(serviceId)
        } else {
            servicingAdapter.removeServiceFromServicing(serviceId)
        }
    }

    return (
        <Checkbox
            style={{padding: '0px', marginTop: '4px'}}
            checked={index >= 0}
            onChange={handleChange} />
    )
})

export default function ServicingCheckbox({ serviceId }) {
    return <ActualCheckbox serviceId={serviceId} servicingAdapter={servicingAdapter} />
}
