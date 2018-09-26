package project.labs.avviotech.com.sharecare.models;

/**
 * Created by NJX on 3/27/2018.
 */

public class NotificationModel {

    private int id;
    private String place;
    private String type;
    private long timestamp;

    public NotificationModel() {

    }

    public int getId() {
        return id;
    }

    public String getPlace() {
        return place;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setType(String type) {
        this.type = type;
    }
}
