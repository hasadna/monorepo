import React, { Component } from 'react';
import Header from "./Header";
import { MuiThemeProvider } from '@material-ui/core/styles';
import Grid from "@material-ui/core/es/Grid/Grid";
import SocialBar from "./SocialBar";
import UserProfile from "./UserProfile";
import ItemsList from "./ItemsList";
import User from "../User";

class UserPage extends Component {
    constructor(props){
        super(props);
    }



    render() {
        const pageData = require('./users/' + this.props.userName); // TODO: Find a better way to import
        const user = new User(pageData);
        return (
                <MuiThemeProvider theme={this.props.theme}>
                    <Header pageTitle="Volunteers" />
                    <Grid container>
                        <Grid item md>
                            <UserProfile user={user} />
                        </Grid>
                        <Grid item md={2}>
                            <SocialBar user={user} imageUrl={pageData.imageUrl} />
                        </Grid>
                        <Grid item lg={12}>
                            <ItemsList title="Top Contributions"/>
                        </Grid>
                    </Grid>
                </MuiThemeProvider>
            );
    }
}

export default UserPage;
