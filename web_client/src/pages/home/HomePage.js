import React, { Component, Fragment } from 'react'
import Header from '../../components/header/Header'

export default class HomePage extends Component {
    constructor(prps) {
        super(prps)
        this.state = {}
    }
    
    render() {
        return (
            <Fragment>
                <Header />
            </Fragment>
        )
    }
}
