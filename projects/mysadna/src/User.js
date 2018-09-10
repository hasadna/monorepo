import UserMetadata from './UserMetadata'

String.prototype.interpolate = function(params) {
    const names = Object.keys(params);
    const values = Object.values(params);
    return new Function(...names, `return \`${this}\`;`)(...values);
}

export default class User {
    constructor(userData){
       this.parseProfileData(userData);
       this.parseSocialData(userData);
    }

    parseSocialNetwork(type, data) {
        if (UserMetadata.socialMetadata[type] &&
            data['socialNetwork'][type]) {
            this['social'][type] = {
                url: data['socialNetwork'][type]['url'],
                icon: UserMetadata.socialMetadata[type]['icon']
            };
        }
    }

    parseSocialData = data => {
        this['social'] = [];
        Object.keys(data['socialNetwork'])
            .map(type => this.parseSocialNetwork(type, data));
    };

    parseProfileField = (fieldMetaData, data) => {
        const attributeName = fieldMetaData['attributeName'];
        this[attributeName] = {};
        if(data[attributeName]) {
            this[attributeName]['label'] = fieldMetaData['label'];
            this[attributeName]['icon'] = fieldMetaData['icon'];
            this[attributeName]['value'] = data[attributeName];
            if(fieldMetaData['url'])
                this[attributeName]['url'] = fieldMetaData['url'].interpolate({value: data[attributeName]});
        }
    };

    parseProfileData = userData => {
        UserMetadata.profileMetaData.map(field => {
           this.parseProfileField(field, userData);
        });
    };
}
