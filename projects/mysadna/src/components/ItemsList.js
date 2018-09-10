import React, { Component } from 'react';
import List from "@material-ui/core/es/List/List";
import ListItem from "@material-ui/core/es/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/es/ListItemIcon/ListItemIcon";
import {LinkVariant} from "mdi-material-ui";
import ListItemText from "@material-ui/core/es/ListItemText/ListItemText";
import ListSubheader from "@material-ui/core/es/ListSubheader/ListSubheader";


class ItemsList extends Component {

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
                {this.link("LINK-DESCRIPTION", "LINK-VALUE")}
            </List>
        );
    }
}

export default ItemsList;
