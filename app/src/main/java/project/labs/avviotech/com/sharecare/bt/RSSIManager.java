package project.labs.avviotech.com.sharecare.bt;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import project.labs.avviotech.com.sharecare.utils.Constants;

/**
 * Created by swayamagrawal on 30/07/17.
 */
public class RSSIManager {

    public RSSIManager()
    {
        rssiMap = new ConcurrentHashMap<>();
        rssiMapDistance = new ConcurrentHashMap<>();
    }
    private ConcurrentHashMap<String,ArrayList<Integer>> rssiMap;
    private ConcurrentHashMap<String,Double> rssiMapDistance;

    public ConcurrentHashMap<String, Double> getRssiMapDistance() {
        return rssiMapDistance;
    }

    public void setRssiMapDistance(ConcurrentHashMap<String, Double> rssiMapDistance) {
        this.rssiMapDistance = rssiMapDistance;
    }

    public ConcurrentHashMap<String, ArrayList<Integer>> getRssiMap() {
        return rssiMap;
    }


    public void setRssiMap(ConcurrentHashMap<String, ArrayList<Integer>> rssiMap) {
        this.rssiMap = rssiMap;
    }

    public ArrayList<Integer> getRssiList(String address)
    {
        if(rssiMap.get(address) ==null)
        {
            ArrayList<Integer> rssiList = new ArrayList<>();
            rssiMap.put(address,rssiList);
        }

        ArrayList<Integer> rssiList = rssiMap.get(address);

        return rssiList;
    }

    public void addRSSI(String address, Integer value)
    {
        ArrayList<Integer> list = getRssiList(address);
        list.add(value);
        rssiMap.put(address, list);

        calDistance(address);
    }

    public double getDistance(String address)
    {
        return rssiMapDistance.get(address);
    }
    public void calDistance(String address)
    {
        double distance = 0;
        ArrayList<Integer> list = getRssiList(address);

        int sum = 0;
        if(list.size() > 0)
        {
            for(Integer i : list)
                sum = sum + i.intValue();

            double mean = sum / list.size();
            distance = calculateDistance(mean);
            distance = ((double) Math.round(distance * 100.0d)) / 100.0d;

            if(list.size() == 10)
                list.clear();
        }
        rssiMapDistance.put(address,distance);

    }

    protected double calculateDistance(double rssi) {
        if (rssi == 0.0d) {
            return -1.0d;
        }
        int txPower = -70;
        double ratio = (rssi * 1.0d) / ((double) txPower);
        if (ratio < 1.0d) {
            return Math.pow(ratio, 10.0d);
        }
        return  1.35 * ((0.89976 * Math.pow(ratio, 7.7095)) + 0.111);
    }

    public void periodicReadRSSI(String address)
    {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(address) != null) {
                    ConnectionManager manager = Constants.connectionManagerMap.get(address);
                    manager.readRSSI();
                }

                periodicReadRSSI(address);
            }

        },100);
    }

}
