package project.labs.avviotech.com.sharecare.models;

/**
 * Created by NJX on 3/19/2018.
 */

public class SettingModel {

    private String item;
    public boolean isChecked;
    public boolean isSwitch;

    public SettingModel(String item, boolean isChecked, boolean isSwitch) {
        this.item = item;
        this.isChecked = isChecked;
        this.isSwitch = isSwitch;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }
}
