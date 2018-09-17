import React, { PureComponent } from 'react';
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";

class UserProfile extends PureComponent {

    listTextItem = (item) => {
        const content = `${item.label}: ${item.value}`;
        const linkProps = {
            button: true,
            component: "a",
            href: item.url,
            target: "_blank"
        };
        const itemProps = item.url ? linkProps : {};
        return (
            <ListItem {...itemProps}>
                <ListItemIcon>
                    {React.createElement(item.icon)}
                </ListItemIcon>
                <ListItemText inset primary={content} />
            </ListItem>
        );
    };

    items = userData => {
        console.log(userData);
        return Object.keys(userData)
            .map(key => this.listTextItem(userData[key]))
            .reduce((prev, curr) => [prev, curr]);
    };

    render() {
        const { profile } = this.props;
        return (
            <List component="div">
                {this.items(profile)}
            </List>
        );
    }
}

export default UserProfile;
