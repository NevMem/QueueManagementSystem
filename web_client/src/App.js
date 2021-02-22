import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles'
import StartPage from './pages/StartPage'
import DemoPage from './pages/DemoPage'
import HomePage from './pages/home/HomePage'
import { PrivateRoute } from './utils/PrivateRoute'

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
                    <Route path="/login" exact>
                        <StartPage />
                    </Route>
                    <PrivateRoute path="/" component={HomePage} />
                    <PrivateRoute path="/demo" component={DemoPage} />
                </Switch>
            </Router>
        </ThemeProvider>
    );
}

export default App
