import React, { PureComponent } from 'react';

export default class ErrorBoundary extends PureComponent {
    constructor(props) {
        super(props);
        this.state = { hasError: false };
    }

    componentDidCatch(error, info) {
        this.setState({
            hasError : true,
            error    : error,
            info     : info
        });
    }

    render() {
        if (this.state.hasError) {
            return <h1>Error occurred!</h1>;
        }else {
            return this.props.children;
        }
    }
}