package tools.data_store;

import dagger.Component;
import javax.inject.Singleton;

public class DataStoreTool {
  @Singleton
  @Component
  interface DataStoreToolComponent {
    DataStore getDataStore();
  }

  public static void main(String[] args) {
    DataStore dataStore = DaggerDataStoreTool_DataStoreToolComponent.create().getDataStore();
    dataStore.sayHello();
  }
}

