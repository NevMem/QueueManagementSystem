import { Fragment } from 'react'
import { observer } from 'mobx-react'
import localizedString from '../../localization/localizedString'
import SimpleRatingView from '../../components/rating/SimpleRatingView'

const CurrentUserBlock = observer(({ servicingAdapter }) => {
    const currentTicket = servicingAdapter.currentTicket

    if (currentTicket === undefined) {
        return null
    }

    const user = currentTicket.user

    return (
        <Fragment>
            <p className='clientHeader'>{localizedString('client')}</p>

            <div className='userInfoRow'>
                <div className='description'>{localizedString('email')}</div>
                <div>{user.email}</div>
            </div>

            <div className='userInfoRow'>
                <div className='description'>{localizedString('name')}</div>
                <div>{user.name}</div>
            </div>

            <div className='userInfoRow'>
                <div className='description'>{localizedString('surname')}</div>
                <div>{user.surname}</div>
            </div>

            <div className='userInfoRow'>
                <div className='description'>{localizedString('rating')}</div>
                <div><SimpleRatingView entityId={'user_' + user.email} /></div>
            </div>
        </Fragment>
    )
})

export default CurrentUserBlock
