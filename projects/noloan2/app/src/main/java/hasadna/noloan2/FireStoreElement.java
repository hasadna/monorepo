package hasadna.noloan2;


//an element for storing proto type to Firestore
public class FireStoreElement {

    private String base64;

    public FireStoreElement() {
    }

    public FireStoreElement(String base64) {

        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}
