import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route } from 'react-router-dom';
import './index.css';
import App from './components/App';
import ErrorBoundary from './components/ErrorBoundary';
import registerServiceWorker from './registerServiceWorker';
import UserPage from './components/UserPage';
import theme from './themes/theme';
import CssBaseline from '@material-ui/core/CssBaseline';

const UserPageComponent = ({ match }) => {
    return <UserPage theme={theme} userName={match.params.userName} />;
};


ReactDOM.render(
    <ErrorBoundary>
        <BrowserRouter>
            <div>
                <CssBaseline />
                <Route exact path='/' component={App}/>
                <Route exact path='/volunteers/:userName' component={UserPageComponent}/>
            </div>
        </BrowserRouter>
    </ErrorBoundary>
, document.getElementById('root'));
registerServiceWorker();
