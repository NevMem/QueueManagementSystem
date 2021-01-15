import { HashRouter as Router, Switch, Route } from 'react-router-dom'
import StartPage from './pages/StartPage'


function App() {
    return (
        <Router>
            <Switch>
                <Route path="/">
                    <StartPage />
                </Route>
            </Switch>
        </Router>
    );
}

export default App
