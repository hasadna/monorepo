import React, { PureComponent } from 'react';
import Header from "./Header";
import { MuiThemeProvider } from '@material-ui/core/styles';
import Grid from "@material-ui/core/es/Grid/Grid";
import SocialBar from "./SocialBar";
import UserProfile from "./UserProfile";
import ItemsList from "./ItemsList";
import User from "../User";

class UserPage extends PureComponent {
    constructor(props){
        super(props);
        this.state = { user: new User(this.loadUserData()) };
    }

    loadUserData = () => {
            return require('../users/' + this.props.userName);
    };

    render() {
            return (
                <MuiThemeProvider theme={this.props.theme}>
                    <Header pageTitle="Volunteers"/>
                    <Grid container>
                        <Grid item md>
                            <UserProfile profile={this.state.user.profile}/>
                        </Grid>
                        <Grid item md={2}>
                            <SocialBar social={this.state.user.social} imageUrl={this.state.user.imageUrl}/>
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
