package project.labs.avviotech.com.sharecare.models;

/**
 * Created by NJX on 3/12/2018.
 */

public class UserModel {

    private int userID;
    private String userName;
    private String email;
    private String image;
    private double latitude;
    private double longitude;
    private String push_token;
    private String referral_code;

    public UserModel() {

    }

    public int getUserID() {
        return userID;
    }

    public String getPush_token() {
        return push_token;
    }

    public String getUserName() {
        return userName;
    }

    public String getImage() {
        if (image == null)
            image = "";
        return image;
    }

    public String getEmail() {
        if (email == null)
            return "";
        return email;
    }

    public String getReferral_code() {
        return referral_code;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setPush_token(String push_token) {
        this.push_token = push_token;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setReferral_code(String referral_code) {
        this.referral_code = referral_code;
    }
}
