package project.labs.avviotech.com.sharecare.bt;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.exceptions.BleScanException;
import com.polidea.rxandroidble.scan.ScanFilter;
import com.polidea.rxandroidble.scan.ScanResult;
import com.polidea.rxandroidble.scan.ScanSettings;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.models.BTDevice;
import project.labs.avviotech.com.sharecare.utils.Constants;
import project.labs.avviotech.com.sharecare.utils.Util;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by swayamagrawal on 29/07/17.
 */
public class BTManager {

    public BTManager()
    {
        filters = new ArrayList<>();
    }

    private boolean isScanning = false;
    private Subscription scanSubscription;
    private RxBleClient rxBleClient;
    private ArrayList<ScanFilter> filters;

    public void toggleScan(final Context context)
    {
        Handler handler = new Handler(Looper.getMainLooper());

        rxBleClient = ShareCare.getRxBleClient();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isScanning)
                    startScan();
                else
                    stopScan();

                toggleScan(context);
            }
        }, 2000);



    }

    public void startScan()
    {
        isScanning = true;
        ScanSettings scanSettings =  new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                .build();

        updateFilters();
        if(filters != null && filters.size() > 0)
        {
            switch (filters.size())
            {
                case 1:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings
                            ,filters.get(0))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
                case 2:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings
                            ,filters.get(0),filters.get(1))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
                case 3:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings
                            ,filters.get(0),filters.get(1),filters.get(2))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
                case 4:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings
                            ,filters.get(0),filters.get(1),filters.get(2),filters.get(3))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
                case 5:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings
                            ,filters.get(0),filters.get(1),filters.get(2),filters.get(3),filters.get(4))
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
                default:
                    scanSubscription = rxBleClient.scanBleDevices(scanSettings)
                            .observeOn(AndroidSchedulers.mainThread())
                            .doOnUnsubscribe(this::clearSubscription)
                            .subscribe(this::onScanResult, this::onScanFailure);
                    break;
            }
        }




    }

    public void stopScan()
    {
        isScanning = false;
        if(scanSubscription != null)
        {
            scanSubscription.unsubscribe();
            scanSubscription = null;
        }
    }

    private void handleBleScanException(BleScanException bleScanException) {

        switch (bleScanException.getReason()) {
            case BleScanException.BLUETOOTH_NOT_AVAILABLE:
                break;
            case BleScanException.BLUETOOTH_DISABLED:
                break;
            case BleScanException.LOCATION_PERMISSION_MISSING:
                break;
            case BleScanException.LOCATION_SERVICES_DISABLED:
                break;
            case BleScanException.SCAN_FAILED_ALREADY_STARTED:
                break;
            case BleScanException.SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                break;
            case BleScanException.SCAN_FAILED_FEATURE_UNSUPPORTED:
                break;
            case BleScanException.SCAN_FAILED_INTERNAL_ERROR:
                break;
            case BleScanException.SCAN_FAILED_OUT_OF_HARDWARE_RESOURCES:
                break;
            case BleScanException.UNKNOWN_ERROR_CODE:
            case BleScanException.BLUETOOTH_CANNOT_START:
            default:
                break;
        }
    }

    public void onScanFailure(Throwable throwable) {

        if (throwable instanceof BleScanException) {
            handleBleScanException((BleScanException) throwable);
        }
    }

    public void onScanResult(ScanResult bleScanResult) {

        if(bleScanResult != null)
        {
            Log.i(Constants.TAG,"Device - " + bleScanResult.getBleDevice().getMacAddress());
            if(Constants.connectionManagerMap == null)
                Constants.connectionManagerMap = new ConcurrentHashMap<>();

            if(Constants.connectionManagerMap.get(bleScanResult.getBleDevice().getMacAddress()) == null)
            {
                ConnectionManager m = new ConnectionManager();
                Constants.connectionManagerMap.put(bleScanResult.getBleDevice().getMacAddress(), m);
            }
            ShareCare.rssiManager.addRSSI(bleScanResult.getBleDevice().getMacAddress(),bleScanResult.getRssi());

            String deviceNAme = Util.getDeivceName(bleScanResult.getBleDevice().getMacAddress());
            if(deviceNAme.indexOf("PETTAG") != -1 || deviceNAme.indexOf("FLEXCLIP") != -1 || deviceNAme.indexOf("KEYFOB") != -1)
            {
                if(Util.shouldConnect(bleScanResult.getBleDevice().getMacAddress()))
                {
                    ConnectionManager manager = Constants.connectionManagerMap.get(bleScanResult.getBleDevice().getMacAddress());
                    manager.connectToDevice(bleScanResult.getBleDevice().getMacAddress(),ShareCare.context,bleScanResult.getBleDevice());
                }
            }
            else
            {
                if(Util.isDeviceConnected("PETTAG") || Util.isDeviceConnected("FLEXCLIP"))
                {
                    ConnectionManager manager = Constants.connectionManagerMap.get(bleScanResult.getBleDevice().getMacAddress());
                    manager.connectToDevice(bleScanResult.getBleDevice().getMacAddress(),ShareCare.context,bleScanResult.getBleDevice());
                }
            }
        }

    }

    public void clearSubscription() {
        scanSubscription = null;
    }


    public void updateFilters()
    {
        filters.clear();

        if(Constants.deviceMap != null)
        {

            for (Map.Entry<String, BTDevice> entry : Constants.deviceMap.entrySet()) {
                String deviceName = entry.getValue().getName();
                if(deviceName.indexOf("DOORSENSOR") != -1 || deviceName.indexOf("DRIVERDOORSENSOR") != -1)
                {

                    if(Util.isDeviceConnected("PETTAG") || Util.isDeviceConnected("FLEXCLIP"))
                    {
                        Log.i(Constants.TAG,deviceName);
                        filters.add(new ScanFilter.Builder().setDeviceAddress(entry.getKey()).build());
                    }

                }
                else
                    filters.add(new ScanFilter.Builder().setDeviceAddress(entry.getKey()).build());
            }
        }
    }

    public void resetFilters()
    {
        filters.clear();
    }

}
