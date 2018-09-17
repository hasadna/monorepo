import React, { PureComponent } from 'react';
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import {LinkVariant} from "mdi-material-ui";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import ListSubheader from "@material-ui/core/ListSubheader/ListSubheader";


class ItemsList extends PureComponent {

    link = (text, url) => {
        return (
            <ListItem button component="a" href={url} target="_blank">
                <ListItemIcon>
                    <LinkVariant />
                </ListItemIcon>
                <ListItemText inset primary={text} />
            </ListItem>
        );
    };

    render() {
        return (
            <List component="nav" subheader={<ListSubheader>{this.props.title}</ListSubheader>}>
                {/* TODO: Load links dynamically via User object*/}
                {this.link("LINK-DESCRIPTION", "LINK-VALUE")}
            </List>
        );
    }
}

export default ItemsList;
