import React, { PureComponent } from 'react';
import Header from "./Header";

class App extends PureComponent {
  render() {
    return (
      <div className="App">
        <Header pageTitle="Main Page"/>
      </div>
    );
  }
}

export default App;
