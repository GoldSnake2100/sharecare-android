package project.labs.avviotech.com.sharecare.models;

/**
 * Created by NJX on 3/26/2018.
 */

public class ChildModel {

    private String name;
    private String image;

    public ChildModel() {
        name = "";
        image = "";
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
