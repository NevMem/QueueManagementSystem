import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles'
import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import { PrivateRoute } from './utils/PrivateRoute'
import DemoPage from './pages/DemoPage'
import FeedbackPage from './pages/feedback/FeedbackPage'
import HomePage from './pages/home/HomePage'
import ProfilePage from './pages/profile/ProfilePage'
import ServicingPage from './pages/servicing/ServicingPage'
import SettingsPage from './pages/settings/SettingsPage'
import StartPage from './pages/start/StartPage'
import TablePage from './pages/table/TablePage'

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
                    <PrivateRoute path='/feedback' component={FeedbackPage} />
                    <PrivateRoute path='/table' component={TablePage} />
                    <PrivateRoute path='/servicing' component={ServicingPage} />
                    <PrivateRoute path='/' component={HomePage} />
                </Switch>
            </Router>
        </ThemeProvider>
    );
}

export default App
