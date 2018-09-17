import React, { PureComponent } from 'react';
import List from "@material-ui/core/List/List";
import ListItem from "@material-ui/core/ListItem/ListItem";
import ListItemIcon from "@material-ui/core/ListItemIcon/ListItemIcon";
import {LinkVariant} from "mdi-material-ui";
import ListItemText from "@material-ui/core/ListItemText/ListItemText";
import ListSubheader from "@material-ui/core/ListSubheader/ListSubheader";


class ItemsList extends PureComponent {

    createLink = (text, url) => {
        return (
            <ListItem button component="a" href={url} target="_blank">
                <ListItemIcon>
                    <LinkVariant />
                </ListItemIcon>
                <ListItemText inset primary={text} />
            </ListItem>
        );
    };

    createLinks = links => {
        return links
            .map(link => this.createLink(link.description, link.url))
            .reduce((prev, curr) => [prev, curr]);
    };

    render() {
        const { links } = this.props;
        return (
            <List component="nav" subheader={<ListSubheader>{this.props.title}</ListSubheader>}>
                {this.createLinks(links)}
            </List>
        );
    }
}

export default ItemsList;
