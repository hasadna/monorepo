import React, { Component } from 'react';
import AppBar from '@material-ui/core/es/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

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

export default Header;
