package rapid.responce.RUCU;

public class Approval {
    private String requestId;
    private String senderID;
    private String TimeStamp;
    private String garageName;
    private String status;


    public Approval() {
        // Required empty constructor for Firestore
    }

    public Approval(String requestId, String TimeStamp, String status, String senderID, String garageName) {
        this.requestId = requestId;
        this.TimeStamp = TimeStamp;
        this.status = status;
        this.senderID= senderID;
        this.garageName = garageName;
    }

    // Getters and setters

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getGarageName() {
        return garageName;
    }

    public void setTimeStamp(String timeStamp) {
        this.TimeStamp = TimeStamp;
    }


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public void setGarageName(String garageName) {
        this.garageName = garageName;
    }
}
