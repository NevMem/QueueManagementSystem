import React from 'react'
import './Header.css'
import Avatar from '../avatar/Avatar'
import SettingsButton from '../buttons/settings/SettingsButton'

export default function Header() {
    return (
        <div className='header'>
            <div className='headerAppName'>QMS</div>
            <Avatar className='headerAvatar' />
            <SettingsButton className='headerSettingsButton' />
        </div>
    )
}
