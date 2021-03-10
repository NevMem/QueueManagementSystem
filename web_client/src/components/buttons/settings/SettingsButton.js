import settingsImage from './settings.svg'
import './SettingsButton.css'

export default function SettingsButton({onClick, ...props}) {
    return (
        <img className='settingsButton' alt='settings' onClick={onClick} src={settingsImage} {...props} />
    )
}