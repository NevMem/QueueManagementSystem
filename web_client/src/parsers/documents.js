
const extractByName = (data, name, type) => {
    if (data[name] !== undefined) {
        return { ...JSON.parse(data[name]), type: type }
    }
    return undefined
}

const extractDocuments = (user) => {
    const data = user.data
    return [
        extractByName(data, 'passport', 'passport'),
        extractByName(data, 'internationalPassport', 'internationalPassport'),
        extractByName(data, 'tin', 'tin'),
        extractByName(data, 'healthInsurancePolicy', 'healthInsurancePolicy'),
    ].filter(elem => elem)
}

export default extractDocuments
