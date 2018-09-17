import React, { PureComponent } from 'react';
import Header from "./Header";
import UsersList from "./UsersList";

class App extends PureComponent {
  render() {
    return (
      <div className="App">
        <Header pageTitle="Main Page"/>
        <div>Here users</div>
        <div>Here projects </div>
        <UsersList></UsersList>
      </div>
    );
  }
}

export default App;
