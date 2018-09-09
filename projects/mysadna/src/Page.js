import React, { Component } from 'react';
import Header from "./Header";
import { MuiThemeProvider } from '@material-ui/core/styles';
import List from '@material-ui/core/List';
import ListItem from '@material-ui/core/ListItem';
import ListItemIcon from '@material-ui/core/ListItemIcon';
import ListItemText from '@material-ui/core/ListItemText';
import FaceProfile from 'mdi-material-ui/FaceProfile';
import Pro from 'mdi-material-ui/ProfessionalHexagon';
import GitHub from 'mdi-material-ui/GithubCircle';
import Email from 'mdi-material-ui/Email';
import Label from 'mdi-material-ui/Label';


class Page extends Component {
    constructor(props){
        super(props);
    }

    listItem = (icon, label, value) => {
        const listItemContent = `${label}: ${value}`;
      return (
          <ListItem>
              <ListItemIcon>
                  {React.createElement(icon)}
              </ListItemIcon>
              <ListItemText inset primary={listItemContent} />
          </ListItem>
      );
    };

    render() {
        const pageData = require('./volunteers/' + this.props.userName); // TODO: Find a better way to import
        return (
                <MuiThemeProvider theme={this.props.theme}>
                    <Header pageTitle="Volunteers" />
                    <List component="nav">
                        {this.listItem(FaceProfile, "First Name", pageData.firstName)}
                        {this.listItem(FaceProfile, "Last Name", pageData.lastName)}
                        {this.listItem(Email, "Email", pageData.email)}
                        {this.listItem(Pro, "Skills", pageData.skill)}
                        {this.listItem(GitHub, "GitHub", pageData.githubUser)}
                        {this.listItem(Label, "Project", pageData.projectId)}
                        {this.listItem(Label, "Image", pageData.imageUrl)}
                    </List>
                </MuiThemeProvider>
            );
    }
}

export default Page;
