import defaultImage from './default.svg'

export default function Avatar(props) {
    return (
        <img alt='Avatar' src={defaultImage} {...props} />
    )
}
