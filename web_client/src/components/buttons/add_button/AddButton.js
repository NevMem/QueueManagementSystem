import './AddButton.css'
import iconBlack from './add_icon_black.svg'
import iconWhite from './add_icon_white.svg'

export default function AddButton({isPrimaryButton, text, ...props}) {
    var className = 'addButton'
    if (!isPrimaryButton) {
        className = 'addButtonSecondary'
    }

    var imageSrc = iconBlack
    if (!isPrimaryButton) {
        imageSrc = iconWhite
    }

    var fontColor = '#000000'
    if (!isPrimaryButton) {
        fontColor = '#ffffff'
    }

    return (
        <div className={className} {...props}>
            <img alt='add image' src={imageSrc} style={{display: 'inline-block'}} />
            <div
                style={{
                    fontSize: '16px',
                    marginLeft: '5px',
                    display: 'inline-block',
                    color: fontColor,
                    whiteSpace: 'nowrap',
                    overflow: 'hidden'
                }}>
                    {text}
            </div>
        </div>
    )
}
