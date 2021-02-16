import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import { createMuiTheme, ThemeProvider } from '@material-ui/core/styles'
import StartPage from './pages/StartPage'
import DemoPage from './pages/DemoPage'

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
                    <Route path="/" exact>
                        <StartPage />
                    </Route>
                    <Route path="/demo">
                        <DemoPage />
                    </Route>
                </Switch>
            </Router>
        </ThemeProvider>
    );
}

export default App
