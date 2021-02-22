import { Component } from 'react'
import { Route } from 'react-router-dom'
import authAdapter from '../adapters/AuthAdapter'

export const PrivateRoute = ({ component: Component, roles, ...rest }) => (
    <Route {...rest} render={props => {
        const currentUser = authAdapter.currentUser()
        if (!currentUser) {
            return <Redirect to={{ pathname: '/login', state: { from: props.location } }} />
        }

        if (roles && roles.indexOf(currentUser.role) === -1) {
            return <Redirect to={{ pathname: '/'}} />
        }

        return <Component {...props} />
    }} />
)
