import React from 'react';
import ReactDOM from 'react-dom';
import { BrowserRouter, Route } from 'react-router-dom';
import './index.css';
import App from './components/App';
import ErrorBoundary from './components/ErrorBoundary';
import registerServiceWorker from './registerServiceWorker';
import UserPage from './components/UserPage';
// import ProjectPage from './components/ProjectPage';
import theme from './themes/theme';
import CssBaseline from '@material-ui/core/CssBaseline';

const UserPageComponent = ({ match }) => {
    return <UserPage theme={theme} userName={match.params.userName} />;
};
// const ProjectPageComponent = ({ match }) => {
//     return <ProjectPage theme={theme} projectName={match.params.ProjectName} />;
// };

ReactDOM.render(    
    <BrowserRouter>
        <div>
            <CssBaseline />
            <Route exact path='/' component={App}/>
            <Route exact path='/volunteers/:userName' component={UserPageComponent}/>
        </div>
    </BrowserRouter>
  
, document.getElementById('root'));
// <Route exact path='/Projects/:ProjectName' component={ProjectPageComponent}/>
registerServiceWorker();