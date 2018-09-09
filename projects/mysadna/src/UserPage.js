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
import Card from '@material-ui/core/Card';
import CardMedia from '@material-ui/core/CardMedia';

class UserPage extends Component {
    constructor(props){
        super(props);
    }

    listTextItem = (icon, label, value) => {
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
                    <Card style={{maxWidth:460, float:"right", padding:"2px", margin:"15px"}}>
                        <CardMedia
                            component="img"
                            style={{maxWidth:460, float:"right"}}
                            image={pageData.imageUrl}
                            title={`${pageData.firstName} ${pageData.lastName}`}
                        />
                    </Card>
                    <List component="nav" style={{float:"left", width:"50%", margin:"15px"}}>
                        {this.listTextItem(FaceProfile, "First Name", pageData.firstName)}
                        {this.listTextItem(FaceProfile, "Last Name", pageData.lastName)}
                        {this.listTextItem(Email, "Email", pageData.email)}
                        {this.listTextItem(Pro, "Skills", pageData.skill)}
                        {this.listTextItem(GitHub, "GitHub", pageData.githubUser)}
                        {this.listTextItem(Label, "Project", pageData.projectId)}
                    </List>
                </MuiThemeProvider>
            );
    }
}

export default UserPage;
