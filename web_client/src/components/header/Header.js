import React from 'react'
import './Header.css'
import Avatar from '../avatar/Avatar'
import SettingsButton from '../buttons/settings/SettingsButton'
import LocalizationGroup from '../localization-group/LocalizationGroup'

export default function Header() {
    return (
        <div className='header'>
            <div className='headerAppName'>QMS</div>
            <Avatar className='headerAvatar' />
            <SettingsButton className='headerSettingsButton' />
            <LocalizationGroup style={{marginTop: '18px', display: 'inline-block', float: 'right', marginRight: '21px'}} />
        </div>
    )
}
