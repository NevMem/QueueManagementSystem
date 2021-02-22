import iconWhite from './add_icon_white.svg'
import iconBlack from './add_icon_black.svg'
import './AddButton.css'

export default function AddButton({isPrimaryButton, text, ...props}) {
    var className = 'addButton'
    if (!isPrimaryButton) {
        className = 'addButtonSecondary'
    }

    var imageSrc = iconBlack
    if (!isPrimaryButton) {
        imageSrc = iconWhite
    }

    return (
        <div className={className}>
            <img alt='add image' src={imageSrc} style={{display: 'inline-block'}} />
            <div style={{fontSize: '16px', marginLeft: '5px', display: 'inline-block'}}>{text}</div>
        </div>
    )
}
