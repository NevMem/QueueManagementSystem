import settingsImage from './settings.svg'
import './SettingsButton.css'

export default function SettingsButton({onClick: onClick, ...props}) {
    console.log(props, onClick)
    return (
        <img className='settingsButton' alt='settings' onClick={onClick} src={settingsImage} {...props} />
    )
}