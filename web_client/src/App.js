import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles'
import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import { PrivateRoute } from './utils/PrivateRoute'
import DemoPage from './pages/DemoPage'
import HomePage from './pages/home/HomePage'
import ProfilePage from './pages/profile/ProfilePage'
import SettingsPage from './pages/settings/SettingsPage'
import StartPage from './pages/start/StartPage'

const theme = createMuiTheme({
    palette: {
        type: 'dark',
        primary: {
            main: '#90caf9'
        },
    },
})

function App() {
    return (
        <ThemeProvider theme={theme}>
            <Router>
                <Switch>
                    <Route path='/login' exact>
                        <StartPage />
                    </Route>
                    <PrivateRoute path='/demo' component={DemoPage} />
                    <PrivateRoute path='/settings' component={SettingsPage} />
                    <PrivateRoute path='/profile' component={ProfilePage} />
                    <PrivateRoute path='/' component={HomePage} />
                </Switch>
            </Router>
        </ThemeProvider>
    );
}

export default App
