import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles'
import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import { PrivateRoute } from './utils/PrivateRoute'
import DemoPage from './pages/DemoPage'
import HomePage from './pages/home/HomePage'
import SettingsPage from './pages/settings/SettingsPage'
import StartPage from './pages/start/StartPage'

const theme = createMuiTheme({
    palette: {
        type: 'dark',
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
                    <PrivateRoute path='/' component={HomePage} />
                </Switch>
            </Router>
        </ThemeProvider>
    );
}

export default App
