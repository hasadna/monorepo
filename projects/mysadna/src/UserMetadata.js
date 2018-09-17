import {
    FaceProfile,
    Email,
    GithubCircle,
    Label,
    FacebookBox,
    Twitter,
    Instagram,
    Linkedin,
    GooglePlus
} from "mdi-material-ui";
import Pro from "mdi-material-ui/ProfessionalHexagon";

class UserMetadata {
    // TODO: Change to nested json instead of array of jsons
    static profileMetaData = [
        {
            icon: FaceProfile,
            label: "First Name",
            attributeName: "firstName",
        },
        {
            icon: FaceProfile,
            label: "Last Name",
            attributeName: "lastName",
        },
        {
            icon: Email,
            label: "Email",
            attributeName: "email",
        },
        {
            icon: Pro,
            label: "Skills",
            attributeName: "skill",
        },
        {
            icon: GithubCircle,
            label: "GitHub",
            attributeName: "githubUser",
            url: "http://www.github.com/${value}"
        },
        {
            icon: Label,
            label: "Project",
            attributeName: "projectId",
            url: "http://www.project.com/${value}"
        }
    ];

    static socialMetadata = {
        facebook: {
            icon: FacebookBox
        },
        twitter: {
            icon: Twitter
        },
        instagram: {
            icon: Instagram
        },
        linkedin: {
            icon: Linkedin
        },
        googlePlus: {
            icon: GooglePlus
        }
    };
}

export default UserMetadata;