package rapid.responce.RUCU;

import java.util.Date;

public class Request {
    private String requestId;
    private String itemId;
    private String userId;
    private String username;
    private String locationDescription;
    private String problemDescription;
    private String automobileType;
    private Date timestamp;

    // Empty constructor required for Firestore serialization
    public Request() {
    }

    public Request(String requestId, String itemId, String userId, String username, String locationDescription, String problemDescription, String automobileType, Date timestamp) {
        this.requestId = requestId;
        this.itemId = itemId;
        this.userId = userId;
        this.username = username;
        this.locationDescription = locationDescription;
        this.problemDescription = problemDescription;
        this.automobileType = automobileType;
        this.timestamp = timestamp;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getAutomobileType() {
        return automobileType;
    }

    public void setAutomobileType(String automobileType) {
        this.automobileType = automobileType;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
