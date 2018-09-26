package project.labs.avviotech.com.sharecare.models;

import com.polidea.rxandroidble.RxBleDevice;

import rx.Subscription;

/**
 * Created by swayamagrawal on 29/07/17.
 */
public class BTDevice {

    private String name;
    private String address;
    private String type;
    private String batteryValue;
    private String temperature;
    private RxBleDevice bleDevice;
    private Subscription connectionSubscription;

    public BTDevice(String n, String a, String t)
    {
        this.address = a;
        this.temperature = t;
        this.name = n;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getBatteryValue() {
        return batteryValue;
    }

    public void setBatteryValue(String batteryValue) {
        this.batteryValue = batteryValue;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public RxBleDevice getBleDevice() {
        return bleDevice;
    }

    public void setBleDevice(RxBleDevice bleDevice) {
        this.bleDevice = bleDevice;
    }

    public Subscription getConnectionSubscription() {
        return connectionSubscription;
    }

    public void setConnectionSubscription(Subscription connectionSubscription) {
        this.connectionSubscription = connectionSubscription;
    }
}
