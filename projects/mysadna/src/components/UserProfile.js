import React, { Component } from 'react';
import GitHub from "mdi-material-ui/GithubCircle";
import FaceProfile from "mdi-material-ui/FaceProfile";
import Pro from "mdi-material-ui/ProfessionalHexagon";
import Label from "mdi-material-ui/Label";
import Email from "mdi-material-ui/Email";
import List from "@material-ui/core/es/List/List";
import ListItem from "@material-ui/core/es/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/es/ListItemIcon/ListItemIcon";
import ListItemText from "@material-ui/core/es/ListItemText/ListItemText";

class UserProfile extends Component {

    listTextItem = (icon, text, value, url) => {
        const listItemContent = `${text}: ${value}`;
        const linkProps = {
            button: true,
            component: "a",
            href: url,
            target: "_blank"
        };
        const itemProps = url ? linkProps : {};
        return (
            <ListItem {...itemProps}>
                <ListItemIcon>
                    {React.createElement(icon)}
                </ListItemIcon>
                <ListItemText inset primary={listItemContent} />
            </ListItem>
        );
    };

    render() {
        const { user } = this.props;
        return (
            <List component="div">
                {this.listTextItem(FaceProfile, user.firstName.label, user.firstName.value)}
                {this.listTextItem(FaceProfile, user.lastName.label, user.lastName.value)}
                {this.listTextItem(Email, user.email.label, user.email.value)}
                {this.listTextItem(Pro, user.skill.label, user.skill.value)}
                {this.listTextItem(GitHub, user.githubUser.label, user.githubUser.value, user.githubUser.url)}
                {this.listTextItem(Label, user.projectId.label, user.projectId.value, user.projectId.url)}
            </List>
        );
    }
}

export default UserProfile;
