import UserMetadata from './UserMetadata'

export default class User {
    constructor(userData){
       this.parseProfileData(userData);
       this.parseSocialNetworks(userData);
       this.imageUrl = userData.imageUrl;
    }

    static evalTemplateString = function(templateString, params) {
        const names = Object.keys(params);
        const values = Object.values(params);
        return new Function(...names, `return \`${templateString}\`;`)(...values);
    };

    parseSocialNetworks = data => {
        this.social = [];
        Object.keys(data.socialNetwork)
            .map(type => this.parseSocialNetwork(type, data));
    };

    parseSocialNetwork(type, data) {
        if (UserMetadata.socialMetadata[type] &&
            data['socialNetwork'][type]) {
            this.social[type] = {
                url: data['socialNetwork'][type]['url'],
                icon: UserMetadata.socialMetadata[type]['icon']
            };
        }
    }

    parseProfileData = userData => {
        this['profile'] = {};
        UserMetadata.profileMetaData.map(field => {
            this.parseProfileField(field, userData);
        });
    };

    parseProfileField = (fieldMetaData, data) => {
        const profileData = this['profile'];
        const attributeName = fieldMetaData.attributeName;
        profileData[attributeName]= {};
        if(data[attributeName]) {
            profileData[attributeName]['label'] = fieldMetaData['label'];
            profileData[attributeName]['icon'] = fieldMetaData['icon'];
            profileData[attributeName]['value'] = data[attributeName];
            if(fieldMetaData['url'])
                profileData[attributeName]['url'] = User.evalTemplateString(fieldMetaData['url'], {value: data[attributeName]});
        }
    };
}
