package project.labs.avviotech.com.sharecare.models;

/**
 * Created by NJX on 3/15/2018.
 */

public class PlaceModel {

    private int id;
    private String name;
    private String addrss;
    private double longitude;
    private double latitude;

    public PlaceModel() {
        id = 0;
    }

    public String getAddrss() {
        return addrss;
    }

    public int getId() {
        return id;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }

    public void setAddrss(String addrss) {
        this.addrss = addrss;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setName(String name) {
        this.name = name;
    }
}
