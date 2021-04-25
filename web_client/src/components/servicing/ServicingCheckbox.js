import Checkbox from '@material-ui/core/Checkbox'
import { observer } from 'mobx-react'
import servicingAdapter from '../../adapters/ServicingAdapter'

const ActualCheckbox = observer(({ servicingAdapter, organizationId, serviceId }) => {
    const elem = servicingAdapter.serviceIds.find(elem => elem.serviceId === serviceId)

    const handleChange = (event) => {
        if (event.target.checked) {
            servicingAdapter.addServiceToServicing(organizationId, serviceId)
        } else {
            servicingAdapter.removeServiceFromServicing(serviceId)
        }
    }

    return (
        <Checkbox
            style={{padding: '0px', marginTop: '4px'}}
            checked={elem !== undefined}
            onChange={handleChange} />
    )
})

export default function ServicingCheckbox({ serviceId, organizationId }) {
    return <ActualCheckbox serviceId={serviceId} organizationId={organizationId} servicingAdapter={servicingAdapter} />
}
