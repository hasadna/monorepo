package tools.data_store;

import javax.inject.Inject;

public class DataStore {

  @Inject
  public DataStore() {}

  public void sayHello() {
    System.out.println("Hello world!");
  }
}

