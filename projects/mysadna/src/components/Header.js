import React, { PureComponent } from 'react';
import AppBar from '@material-ui/core/AppBar';
import Toolbar from '@material-ui/core/Toolbar';
import Typography from '@material-ui/core/Typography';

class Header extends PureComponent {

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
