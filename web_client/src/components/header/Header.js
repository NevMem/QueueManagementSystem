import './Header.css'
import Avatar from '../avatar/Avatar'
import LocalizationGroup from '../localization-group/LocalizationGroup'
import React from 'react'
import SettingsButton from '../buttons/settings/SettingsButton'
import { Link } from 'react-router-dom'

export default function Header() {
    return (
        <div className='header'>
            <Link to='/'><div className='headerAppName'>QMS</div></Link>
            <Avatar className='headerAvatar' />
            <Link to='/settings'>
                <SettingsButton className='headerSettingsButton' />
            </Link>
            <LocalizationGroup style={{marginTop: '18px', display: 'inline-block', float: 'right', marginRight: '21px'}} />
        </div>
    )
}
