import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route } from 'react-router-dom';
import './index.css';
import App from './App';
import registerServiceWorker from './registerServiceWorker';
import Page from './Page';
import theme from './themes/theme';
import CssBaseline from '@material-ui/core/CssBaseline';

const UserPage = ({ match }) => {
    return <Page theme={theme} userName={match.params.userName} />;
};


ReactDOM.render(    
    <BrowserRouter>
        <div>
            <CssBaseline />
            <Route exact path='/' component={App}/>
            <Route exact path='/volunteers/:userName' component={UserPage}/>
        </div>
    </BrowserRouter>
, document.getElementById('root'));
registerServiceWorker();
