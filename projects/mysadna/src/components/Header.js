import React, { PureComponent } from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';
import PropTypes from 'prop-types'

class Header extends Component {
    
    render() {
        return (
            <AppBar position="static">
                <Toolbar>
                    <Typography variant="title" color="inherit">
                        My Sadna - {this.props.pageTitle}
                    </Typography>
                </Toolbar>
            </AppBar>
        );
    }
}

Header.prototypes ={
    pageTitle: PropTypes.number
}

Header.defaultProps = {
    pageTitle: 'other name'
  };

export default Header;
