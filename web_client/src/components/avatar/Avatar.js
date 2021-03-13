import defaultImage from './default.svg'
import { Link } from 'react-router-dom'

export default function Avatar(props) {
    return (
        <Link to='/profile'>
            <img alt='Avatar' src={defaultImage} {...props} />
        </Link>
    )
}
