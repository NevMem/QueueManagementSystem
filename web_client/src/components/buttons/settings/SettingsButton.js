import settingsImage from './settings.svg'

export default function SettingsButton({onClick: onClick, ...props}) {
    console.log(props, onClick)
    return (
        <img alt='settings' onClick={onClick} src={settingsImage} {...props} />
    )
}