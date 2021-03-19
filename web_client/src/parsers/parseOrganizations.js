const parseService = (json) => {
    if (!json.info || !json.info.id) {
        return undefined
    }
    const service = {
        id: json.info.id,
        name: json.info.name
    }
    return service
}

const parseServices = (json) => {
    if (!json) {
        return []
    }
    const services = []
    for (const serviceItem of json) {
        const parsed = parseService(serviceItem)
        if (parsed) {
            services.push(parsed)
        }
    }
    return services
}

const parseOrganization = (json) => {
    if (!json.info || !json.info.id) {
        return undefined
    }
    const organization = {
        id: json.info.id,
        name: json.info.name,
        address: json.info.address,
        services: parseServices(json.services)
    }
    return organization
}

const parseOrganizations = (json) => {
    if (!json.organizations) {
        return []
    }

    const organizations = []

    for (const orgItem of json.organizations) {
        const parsed = parseOrganization(orgItem)
        if (parsed) {
            organizations.push(parsed)
        }
    }

    return organizations
}

export default parseOrganizations
