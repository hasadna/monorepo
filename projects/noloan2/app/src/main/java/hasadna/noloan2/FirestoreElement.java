package hasadna.noloan2;

// For storing proto type to Firestore
public class FirestoreElement {

  private String base64;

  public FirestoreElement(String base64) {

    this.base64 = base64;
  }

  public String getBase64() {
    return base64;
  }

  public void setBase64(String base64) {
    this.base64 = base64;
  }
}

