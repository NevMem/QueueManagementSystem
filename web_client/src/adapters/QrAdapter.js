import { withBackendUrl } from '../api/utils'

const paths = withBackendUrl({
    qrCode: '/admin/generate_qr'
})

class QrAdapter {
    imageSrc(organizationId, serviceId) {
        if (serviceId) {
            return paths.qrCode + '?organization=' + organizationId + '&service=' + serviceId
        }
        return paths.qrCode + '?organization=' + organizationId
    }
}

const qrAdapter = new QrAdapter()
export default qrAdapter
