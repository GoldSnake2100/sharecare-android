package project.labs.avviotech.com.sharecare.bt;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.polidea.rxandroidble.RxBleConnection;
import com.polidea.rxandroidble.RxBleDevice;
import com.polidea.rxandroidble.RxBleDeviceServices;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.utils.Constants;
import project.labs.avviotech.com.sharecare.utils.HexString;
import project.labs.avviotech.com.sharecare.utils.Util;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;

public class ConnectionManager {

    private RxBleDevice bleDevice;
    private Subscription connectionSubscription;
    private RxBleConnection connection;
    private Handler handler;
    private String macAddress;
    private ConcurrentHashMap<UUID,BluetoothGattCharacteristic> characterMap;
    private Observable<RxBleConnection> conn;
    private boolean isConnected = false;
    private Integer status = 0;
    private PublishSubject<Void> disconnectTriggerSubject = PublishSubject.create();

    public ConnectionManager()
    {
        handler = new Handler(Looper.getMainLooper());
        characterMap = new ConcurrentHashMap<>();
    }

    public void connectToDevice(String address, Context context, RxBleDevice dev)
    {

        this.bleDevice = dev;
        this.macAddress = address;
        // How to listen for connection state changes
        bleDevice.observeConnectionStateChanges()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnectionStateChange);

        if(isDisconnected())
        {

            connectionSubscription = bleDevice.establishConnection(false)
                    .takeUntil(disconnectTriggerSubject)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnUnsubscribe(this::clearSubscription)
                    .subscribe(this::onConnectionReceived, this::onConnectionFailure);

        }

        Constants.deviceMap.get(address).setBleDevice(bleDevice);



    }


    public void onConnectionFailure(Throwable throwable) {
        //noinspection ConstantConditions
        isConnected = false;
        triggerDisconnect();
        resetAlertStatus();
        throwable.printStackTrace();
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_DISCONNECT, macAddress);
            }
        });

    }

    public void onConnectionReceived(RxBleConnection connection) {
        this.connection = connection;
        resetAlertStatus();
        connection.discoverServices().subscribe(this::onServiceDiscovered, this::onServiceFailure);
    }

    public void onConnectionStateChange(RxBleConnection.RxBleConnectionState newState) {

    }


    public void onMtuReceived(Integer mtu) {

    }

    public void clearSubscription() {
        if (connectionSubscription != null && !connectionSubscription .isUnsubscribed()) {
            connectionSubscription.unsubscribe();
        }

        connectionSubscription = null;

    }

    public void triggerDisconnect() {

        if (connectionSubscription != null && !connectionSubscription .isUnsubscribed()) {
            connectionSubscription.unsubscribe();
        }
        connectionSubscription = null;
        resetAlertStatus();
    }


    public synchronized void onServiceDiscovered(RxBleDeviceServices services) {
        isConnected = true;
       for (BluetoothGattService service : services.getBluetoothGattServices()) {
            final List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

            for (BluetoothGattCharacteristic characteristic : characteristics) {
                characterMap.put(characteristic.getUuid(), characteristic);

            }
        }

        if(characterMap.get(Constants.CHAR_CUSTOM_CONNECTION_UUID) != null)
        {
            connection.writeCharacteristic(Constants.CHAR_CUSTOM_CONNECTION_UUID, getSecurityCommand(macAddress))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<byte[]>() {
                        @Override
                        public void call(byte[] bytes) {

                            initDevice();
                        }
                    }, this::onWriteFailure);

            handler.post(new Runnable() {
                @Override
                public void run() {

                    Util.displayToast(getSecurityCommandAsString(macAddress), ShareCare.context);
                }
            });
        }
        else if(characterMap.get(Constants.CHAR_BIOSENSOR_CONNECTION_UUID) != null)
        {
            connection.writeCharacteristic(Constants.CHAR_BIOSENSOR_CONNECTION_UUID, getSecurityCommand(macAddress))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<byte[]>() {
                        @Override
                        public void call(byte[] bytes) {

                            initDevice();
                        }
                    }, this::onWriteFailure);

            handler.post(new Runnable() {
                @Override
                public void run() {

                    Util.displayToast(getSecurityCommandAsString(macAddress), ShareCare.context);
                }
            });
        }
        else
            initDevice();








    }

    public void initDevice()
    {
        for (Map.Entry<UUID,BluetoothGattCharacteristic> entry : characterMap.entrySet())
        {
            if (isCharacteristicReadable(entry.getValue())) {
                readBluetoothGattCharacteristic(entry.getValue());
            }
        }


        if(characterMap.get(Constants.CHAR_AUTOMATION_IO) != null)
            enableNotification(Constants.CHAR_AUTOMATION_IO);

        if(characterMap.get(Constants.OBD_NOTIFY_CHAR_UUID) != null)
            enableNotification(Constants.OBD_NOTIFY_CHAR_UUID);

        if(characterMap.get(Constants.CHAR_BIOSENSOR_NOTIFY_UUID) != null)
            enableNotification(Constants.CHAR_BIOSENSOR_NOTIFY_UUID);

        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_CONNECT, macAddress);
            }
        });

        String deviceName = Util.getDeivceName(macAddress);
        //if(deviceName.indexOf("OBD") == -1 && deviceName.indexOf("BIOSENSOR") == -1 && deviceName.indexOf("DOORSENSOR") == -1 && deviceName.indexOf("DRIVERDOORSENSOR") == -1)
          //  ShareCareApplication.rssiManager.periodicReadRSSI(macAddress);
    }

    public BluetoothGattCharacteristic getCharacteristic(UUID uuid)
    {
        return characterMap.get(uuid);
    }
    public void readBluetoothGattCharacteristic(BluetoothGattCharacteristic characteristic)
    {

        this.connection.
                readCharacteristic(characteristic)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onReadSuccess, this::onReadFailure);
    }

    public void onReadSuccess(byte[] b) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_READ, macAddress);
            }
        });
    }

    public void onServiceFailure(Throwable throwable) {
        throwable.printStackTrace();
    }


    public boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0;
    }

    public boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    public boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() & (BluetoothGattCharacteristic.PROPERTY_WRITE
                | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) != 0;
    }

    public byte[] getInputBytes(String text) {
        return HexString.hexToBytes(text);
    }

    public void onReadFailure(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void onWriteSuccess(byte[] b) {


        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_WRITE, macAddress);
            }
        });

    }

    public void onWriteFailure(Throwable throwable) {
        throwable.printStackTrace();

    }

    public void onNotificationReceived(byte[] bytes) {


        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_NOTIFY, macAddress);
            }
        });

    }

    public void onNotificationSetupFailure(Throwable throwable) {
        throwable.printStackTrace();
    }

    public void notificationHasBeenSetUp() {

    }

    public synchronized void writeCharacteristic(UUID iCharUuid,
                                    byte[] data)
    {

        connection.writeCharacteristic(iCharUuid, data)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWriteSuccess, this::onWriteFailure);
    }

    public void writeCharacteristic(UUID iCharUuid,
                                    int data)
    {
        byte b[] = new byte[1];
        b[0]  = (byte)data;
        connection.writeCharacteristic(iCharUuid, b)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onWriteSuccess, this::onWriteFailure);
    }

    public void enableNotification(UUID iCharUuid)
    {
        connection.setupNotification(iCharUuid)
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(notificationObservable -> notificationObservable)
                .subscribe(this::onNotificationReceived, this::onNotificationSetupFailure);

    }

    public synchronized byte[] getSecurityCommand(String address)
    {
        String command = getSecurityCommandAsString(address);
        byte b[] = hexStringToByteArray(command);
        return b;
    }
    public synchronized String getSecurityCommandAsString(String address)
    {
        String second = address.substring(3, 5);
        String fifth = address.substring(12, 14);
        String command = fifth.trim() + "3663" + second.trim();
        return command;
    }

    public synchronized byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }


    public boolean isConnected() {
        if(bleDevice != null)
            return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTED;

        return false;
    }

    public boolean isDisconnected() {
        if(bleDevice != null)
            return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.DISCONNECTED;

        return false;
    }

    public boolean isConnecting() {
        if(bleDevice != null)
            return bleDevice.getConnectionState() == RxBleConnection.RxBleConnectionState.CONNECTING;

        return false;
    }

    public void sendBroadCast(int action, String address)
    {

        for(Handler handler :ShareCare.remoteHandlers)
        {
            Bundle bundle = new Bundle();
            bundle.putString("eventType", "onConnectionStateChange");
            Message msg = Message.obtain(handler, action);
            bundle.putString("address", address);
            msg.setData(bundle);
            msg.sendToTarget();
        }

    }

    public void readRSSI()
    {
        if(isConnected())
        {
            connection.readRssi()
                    .subscribe(this::updateRssi, this::onRSSIFailure);
        }

    }

    public void updateRssi(int rssiValue) {
        //Log.i(Constants.TAG, "RSSIi - " + rssiValue);
        ShareCare.rssiManager.addRSSI(macAddress, rssiValue);
        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_READ_RSSI, macAddress);
            }
        });
    }

    public void onRSSIFailure(Throwable throwable) {
        //noinspection ConstantConditions
        Log.i(Constants.TAG,"RSSI Failure - " + bleDevice.getMacAddress());
        throwable.printStackTrace();

        handler.post(new Runnable() {
            @Override
            public void run() {
                sendBroadCast(Constants.HANDLER_ACTION_DISCONNECT, macAddress);
            }
        });

    }

    public void powerOffDevice()
    {
        writeCharacteristic(Constants.CHAR_CUSTOM_SERVICE_UUID, 2);
        triggerDisconnect();
    }
    public void powerSaveDevice()
    {
        writeCharacteristic(Constants.CHAR_CUSTOM_SERVICE_UUID,1);
        triggerDisconnect();
    }
    public void sendAlertOn()
    {
        if(status != 1)
        {
            status = 1;
            writeCharacteristic(Constants.CHAR_ALERT_LEVEL_UUID, 1);
        }
    }
    public void sendAlertOff()
    {
        if(status == 1)
        {
            status = 0;
            writeCharacteristic(Constants.CHAR_ALERT_LEVEL_UUID,0);
        }
    }

    public void resetAlertStatus()
    {
        status = 0;
    }
}
