import { Component } from 'react'
import orgAdapter from '../../adapters/OrgAdapter'

export default class TablePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {
            organizationId: this.props.location.pathname.split('/').pop(),
            loading: true,
        }
    }

    componentDidMount() {
        orgAdapter.tickets(this.state.organizationId)
            .then(data => {
                console.log(data)
            })
    }

    render() {
        return null
    }
}
