package rapid.responce.RUCU;

public class LocationData {
    private String garageName;
    private String requestId;
    private String sendText;

    public LocationData() {
        // Empty constructor needed for Firestore
    }

    public LocationData(String garageName, String requestId, String sendText) {
        this.garageName = garageName;
        this.requestId = requestId;
        this.sendText = sendText;
    }

    public String getGarageName() {
        return garageName;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getSendText() {
        return sendText;
    }
}
