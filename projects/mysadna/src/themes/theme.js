import { createMuiTheme } from '@material-ui/core/styles';
import indigo from '@material-ui/core/colors/indigo';

const theme = createMuiTheme({
    palette: {
        primary: {
            main: '#03A9F4',
            contrastText: '#fff',
        },
        secondary: indigo,
        text: '#fff'
    }
});

export default theme;