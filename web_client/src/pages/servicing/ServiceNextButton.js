import Button from '@material-ui/core/Button'
import localizedString from '../../localization/localizedString'
import servicingAdapter from '../../adapters/ServicingAdapter'

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

export default ServiceNextButton
