package hasadna.noloan2;

// For storing proto type to Firestore
public class FirestoreElement {

  private String proto;

  public FirestoreElement(String proto) {
    this.proto = proto;
  }

  public String getProto() {
    return proto;
  }

  public void setProto(String proto) {
    this.proto = proto;
  }
}

