package project.labs.avviotech.com.sharecare.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.bt.ConnectionManager;
import project.labs.avviotech.com.sharecare.fragments.MapFragment;
import project.labs.avviotech.com.sharecare.fragments.NotificationFragment;
import project.labs.avviotech.com.sharecare.fragments.SettingFragment;
import project.labs.avviotech.com.sharecare.fragments.ShareFragment;
import project.labs.avviotech.com.sharecare.models.NotificationModel;
import project.labs.avviotech.com.sharecare.models.PlaceModel;
import project.labs.avviotech.com.sharecare.models.UserModel;
import project.labs.avviotech.com.sharecare.onesignal.PostNotification;
import project.labs.avviotech.com.sharecare.service.LocationService;
import project.labs.avviotech.com.sharecare.utils.API;
import project.labs.avviotech.com.sharecare.utils.CommonUtil;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.Constants;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;
import project.labs.avviotech.com.sharecare.utils.UiUtil;
import project.labs.avviotech.com.sharecare.utils.Util;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";
    private BottomNavigationView navigation;
    private MapFragment mMapFragment;
    private ShareFragment mShareFragment;
    private NotificationFragment mNotificationFragment;
    private SettingFragment mSettingFragment;
    private TextView mTVTitle;
    private double flexClipDistance  = 0;

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.BROADCAST_LOCATION_STATE)) {
                boolean isEnable = intent.getBooleanExtra("state", true);
                Log.e(TAG, String.valueOf(isEnable));
                SharedPrefUtil.saveBoolean(MainActivity.this, Constant.PREF_LOCATION_ENABLE, isEnable);
                if (isEnable) {
                    startService(new Intent(MainActivity.this, LocationService.class));
                } else {
                    stopService(new Intent(MainActivity.this, LocationService.class));
                }
            } else if (intent.getAction().equals(Constant.BROADCAST_NOTIFICATION)) {
                String place = intent.getStringExtra("place");
                String notification = intent.getStringExtra("notification");
                long time = intent.getLongExtra("time", 0);

                NotificationModel model = new NotificationModel();
                model.setPlace(place);
                model.setTimestamp(time);
                model.setType(notification);
                ShareCare.dbHelper.saveNotification(model);
                Log.e(TAG, model.getPlace() + "/" + model.getType() + "\t" + model.getTimestamp());
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = mMapFragment;
                    changeTitle(getString(R.string.title_home));
                    break;
                case R.id.navigation_share:
                    selectedFragment = mShareFragment;
                    changeTitle(getString(R.string.title_share));
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = mNotificationFragment;
                    changeTitle(getString(R.string.title_notifications));
                    break;
                case R.id.navigation_settings:
                    selectedFragment = mSettingFragment;
                    changeTitle(getString(R.string.title_settings));
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean loginState = SharedPrefUtil.loadBoolean(this, Constant.PREF_AUTOLOGIN, false);
        if (!loginState) {
            Intent i = new Intent(this, LoginActivity.class);
            startActivity(i);
            finish();
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        mTVTitle = toolbar.findViewById(R.id.toolbar_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        changeTitle(getString(R.string.title_home));

        navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        mMapFragment = new MapFragment();
        mShareFragment = new ShareFragment();
        mNotificationFragment = new NotificationFragment();
        mSettingFragment = new SettingFragment();

        startService(new Intent(this, LocationService.class));

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCAST_NOTIFICATION);
        filter.addAction(Constant.BROADCAST_LOCATION_STATE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver, filter);

        ShareCare.btManager.toggleScan(getApplicationContext());

        ShareCare.registerRemoteHandler(mActionHandler);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String notiData = bundle.getString("notiData", "");
            if (!notiData.isEmpty()) {
                treatPushData(notiData);
            }
        } else {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.frame_layout, mMapFragment);
            transaction.commit();
        }

        // test notification
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_ON, "CHILD UNATTENDED ON");
//            }
//        }, 2000);
    }

    public void treatPushData(String notiData) {

        new Handler().postDelayed(() -> {
            try {
                JSONObject object = new JSONObject(notiData);
                String type = object.getString(Constant.PUSH_TYPE);
                if (type.equals(Constant.PUSH_TYPE_SHARE)) {
                    String sender = object.getString("sender");
                    String senderToken = object.getString("sender_token");
                    String url = object.getString("url");
                    navigation.setSelectedItemId(R.id.navigation_share);

                    showDialogForShare(type, sender, url, senderToken);
                } else  if (type.equals(Constant.PUSH_TYPE_INVITE)) {
                    String sender = object.getString("sender");
                    String referralCode = object.getString("referralCode");
                    navigation.setSelectedItemId(R.id.navigation_share);

                    showInviteDialog(sender, referralCode);
                } else if (type.equals(Constant.PUSH_TYPE_ACCEPT)) {
                    String sender = object.getString("sender");
                    String title = sender + " accept your share request";
                    navigation.setSelectedItemId(R.id.navigation_share);

                    Intent i = new Intent();
                    i.putExtra("place", "");
                    i.putExtra("notification", title);
                    i.putExtra("time", System.currentTimeMillis());
                    i.setAction(Constant.BROADCAST_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                    showShareResult(title, getString(R.string.confirm));
                } else if (type.equals(Constant.PUSH_TYPE_REJECT)) {
                    String sender = object.getString("sender");
                    String title = sender + " reject your share request";
                    navigation.setSelectedItemId(R.id.navigation_share);

                    Intent i = new Intent();
                    i.putExtra("place", "");
                    i.putExtra("notification", title);
                    i.putExtra("time", System.currentTimeMillis());
                    i.setAction(Constant.BROADCAST_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                    showShareResult(title, getString(R.string.denied));
                } else if (type.equals(Constant.PUSH_TYPE_CHILD_EVENT_ON) || type.equals(Constant.PUSH_TYPE_CHILD_EVENT_OFF)) {
                    String placeName = object.getString("place");
                    long timestamp = object.getLong("timestamp");
                    String notification = object.getString("notification");
                    Intent i = new Intent();
                    i.putExtra("place", placeName);
                    i.putExtra("notification", notification);
                    i.putExtra("time", timestamp);
                    i.setAction(Constant.BROADCAST_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);
                    navigation.setSelectedItemId(R.id.navigation_notifications);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, 300);
    }

    private void changeTitle(String title) {
        mTVTitle.setText(title);
    }

    private void showInviteDialog(String sender, String referralCode) {
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(sender + " would like to invite you.")
                .setCustomImage(R.drawable.logo)
                .setConfirmText("Accept")
                .setCancelText("Cancel")
                .setConfirmClickListener(sweetAlertDialog -> {
                    referUser(referralCode);
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    private void referUser(String code) {
        int userId = SharedPrefUtil.loadInt(this, Constant.PREF_USER_ID,  0);
        AndroidNetworking.post(API.REFER_USER)
                .addQueryParameter("userId", String.valueOf(userId))
                .addQueryParameter("code", code)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.getString("status").equals("success")) {
                                UiUtil.showShortToast("You registered as CareGiver");
                            } else {
                                UiUtil.showShortToast("Invalid referral code");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

    private void showShareResult(String title, String result) {
        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(title)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(result)
                .setConfirmClickListener(sweetAlertDialog -> {
                    if (result.equals(getString(R.string.confirm))) {
                        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                        if (mBluetoothAdapter.isEnabled()) {
                            mBluetoothAdapter.disable();
                        }
                    }
                    sweetAlertDialog.dismissWithAnimation();
                })
                .show();
    }

    private void showDialogForShare(String type, String sender, String url, String senderToken) {

        new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE)
                .setTitleText(sender + " want to share children with you")
//                .setCustomView(view)
                .setCustomImage(R.drawable.logo)
                .setConfirmText(getString(R.string.accept))
                .setCancelText(getString(R.string.reject))
                .setConfirmClickListener(sweetAlertDialog -> {

                    JSONObject data = new JSONObject();
                    String message = SharedPrefUtil.loadString(MainActivity.this, Constant.PREF_USER_NAME, "");
                    String email = SharedPrefUtil.loadString(MainActivity.this, Constant.PREF_USER_EMAIL, "");
                    if (message.isEmpty())
                        message = email;
                    message += " accept your share request";

                    try {
                        data.put("en", message);
                        data.put("sender", email);
                        data.put(Constant.PUSH_TYPE, Constant.PUSH_TYPE_ACCEPT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> receivers = new ArrayList<>();
                    receivers.add(senderToken);
                    PostNotification.send(message, data, receivers);
                    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    if (!mBluetoothAdapter.isEnabled()) {
                        mBluetoothAdapter.enable();
                    }

                    Intent i = new Intent();
                    i.putExtra("place", "");
                    i.putExtra("notification", "Accepted share request from " + sender);
                    i.putExtra("time", System.currentTimeMillis());
                    i.setAction(Constant.BROADCAST_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                    sweetAlertDialog.dismissWithAnimation();

                })
                .setCancelClickListener(sweetAlertDialog -> {

                    JSONObject data = new JSONObject();
                    String message = SharedPrefUtil.loadString(MainActivity.this, Constant.PREF_USER_NAME, "");
                    String email = SharedPrefUtil.loadString(MainActivity.this, Constant.PREF_USER_EMAIL, "");
                    if (message.isEmpty())
                        message = email;
                    message += " reject your share request";

                    try {
                        data.put("en", message);
                        data.put("sender", email);
                        data.put(Constant.PUSH_TYPE, Constant.PUSH_TYPE_REJECT);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    List<String> receivers = new ArrayList<>();
                    receivers.add(senderToken);
                    PostNotification.send(message, data, receivers);

                    Intent i = new Intent();
                    i.putExtra("place", "");
                    i.putExtra("notification", "Rejected share request from " + sender);
                    i.putExtra("time", System.currentTimeMillis());
                    i.setAction(Constant.BROADCAST_NOTIFICATION);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(i);

                    sweetAlertDialog.dismissWithAnimation();

                })
                .show();
    }

    private Handler mActionHandler = new Handler() {
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String address = bundle.getString("address");
            String deviceName = Util.getDeivceName(address);
            try {
                if (address != null) {
                    synchronized (msg) {
                        switch (msg.what) {
                            case Constants.HANDLER_ACTION_NOTIFY:
                                notifyAction(deviceName,address);
                                break;
                            case Constants.HANDLER_ACTION_CONNECT:
                                updateUI();
                                Util.playSound(getApplicationContext(),3000);
                                //NANIApplication.rssiManager.periodicReadRSSI(address);
                                break;
                            case Constants.HANDLER_ACTION_DISCONNECT:
                                updateUI();
                                Util.disconnectAllDevices();
                                break;
                            case Constants.HANDLER_ACTION_READ:
                                updateUI();
                                checkTempForDevice(address,deviceName);
                                break;
                            case Constants.HANDLER_ACTION_WRITE:
                                updateUI();
                                break;
                            case Constants.HANDLER_ACTION_READ_RSSI:
                                updateRSSI(address,deviceName);
                                break;
                        }
                    }
                }
            }catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    };

    public void updateUI()
    {
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(f instanceof ShareFragment)
        {
            ((ShareFragment)f).updateUI();
        }
    }

    public void updateRSSI(String address, String deviceName)
    {
        double distance = ShareCare.rssiManager.getDistance(address);
        ConnectionManager manager = Util.getConnectionManager(address);
        if(manager != null)
        {
            if(distance > 2)
            {
                if(deviceName.indexOf("KEYFOB") != -1) {
                    if (Util.isDeviceConnected("FLEXCLIP")) {
                        double d = distance - flexClipDistance;
                        if (d > 4)
                            manager.sendAlertOn();
                    }
                }
            }
            if(distance > 4)
            {
                if(deviceName.indexOf("PETTAG") != -1)
                {
                    manager.powerSaveDevice();
                    Util.disconnectAllDevices();
                }
                else if(deviceName.indexOf("FLEXCLIP") != -1)
                {
                    Util.displayToast("Door Locked",getApplicationContext());
                    manager.powerSaveDevice();
                    Util.disconnectAllDevices();
                }
                notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_ON, "CHILD UNATTENDED ON");
            }
            else
            {
                if(deviceName.indexOf("KEYFOB") != -1)
                {
                    if(Util.isDeviceConnected("FLEXCLIP"))
                        manager.sendAlertOff();
                }
                notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_OFF, "CHILD UNATTENDED OFF");
            }
        }

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.frame_layout);
        if(f instanceof ShareFragment)
        {
            //((ShareCareFragment)f).updateDistance();
        }

    }

    public void checkTempForDevice(String address, String deviceName)
    {
        ConnectionManager manager = Util.getConnectionManager(address);
        BluetoothGattCharacteristic tempChar =  manager.getCharacteristic(Constants.CHAR_HTP_TEMPERATURE_MEASUREMENT);
        if (tempChar != null && tempChar.getValue() != null) {
            int temp = Util.convertBytetoInt(tempChar.getValue());
            double fahrenheit = (9.0/5.0)*(temp / 10) + 32;
            double tempValue = new Double(fahrenheit).intValue();

            if(deviceName.indexOf("PETTAG") != -1)
            {
                if(tempValue > 90 && tempValue <= 95)
                    Util.displayToast("Pet In Distress",getApplicationContext());
                else if(tempValue > 95)
                    Util.displayToast("Pet In Danger",getApplicationContext());
            }
            else  if(deviceName.indexOf("FLEXCLIP") != -1)
            {
                if(tempValue > 90 && tempValue <= 95) {
                    Util.displayToast("Child In Distress", getApplicationContext());
                    notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_ON, "CHILD DISTRESS ON");
                }
                else if(tempValue > 95) {
                    Util.displayToast("Child In Danger", getApplicationContext());
                    notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_ON, "CHILD DANGER ON");
                } else {
                    notifyEvent(Constant.PUSH_TYPE_CHILD_EVENT_OFF, "CHILD DISTRESS OFF");
                }

            }

        }
    }

    public void notifyAction(String deviceName,String address)
    {
        if(deviceName.indexOf("FLEXCLIP") != -1)
        {
            ConnectionManager manager = Util.getConnectionManager(address);
            updateClipStatus(manager);
        }
        if(deviceName.indexOf("DOORSENSOR") != -1)
        {
            ConnectionManager manager = Util.getConnectionManager(address);
            BluetoothGattCharacteristic doorChar = manager.getCharacteristic(Constants.CHAR_AUTOMATION_IO);
            if(doorChar != null && doorChar.getValue() != null) {
                int value = doorChar.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                if(value == 1)
                {
                    Util.playSound(getApplicationContext(),0);
                    Util.displayToast("Door Opened", getApplicationContext());
                }
                else if(value == 0)
                {
                    Util.stopSound(getApplicationContext());
                    Util.displayToast("Door Closed",getApplicationContext());
                }
            }

        }
        if(deviceName.indexOf("KEYFOB") != -1)
        {
            ConnectionManager manager = Util.getConnectionManager(address);
            BluetoothGattCharacteristic keyFobChar = manager.getCharacteristic(Constants.CHAR_AUTOMATION_IO);
            if(keyFobChar != null && keyFobChar.getValue() != null) {
                byte[] value = keyFobChar.getValue();

                if(value[0] == 65 && value[1] == -8)
                {
                    Util.displayToast("Panic On", getApplicationContext());
                }
                else if(value[0] == 66 && value[1] == -8)
                {
                    Util.displayToast("Panic Off", getApplicationContext());
                }
            }

        }
    }

    public void updateClipStatus(ConnectionManager manager)
    {
        BluetoothGattCharacteristic doorChar = manager.getCharacteristic(Constants.CHAR_AUTOMATION_IO);
        if(doorChar != null && doorChar.getValue() != null) {
            int value = doorChar.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            if(value == 1)
            {
                Util.displayToast("Clip Opened",getApplicationContext());
                manager.powerOffDevice();
                Util.disconnectAllDevices();

            }
        }
    }

    public void notifyEvent(String eventType, String notification) {
        String placeName = "";
        long timestamp;
        List<PlaceModel> placeModels = ShareCare.dbHelper.getPlaces();
        int distance = 0;
        for (int i = 0; i < placeModels.size(); i++) {
            PlaceModel place = placeModels.get(i);
            distance = CommonUtil.getDistance(MapFragment.myLocationLatLng, new LatLng(place.getLatitude(), place.getLongitude()));
            if (distance < Constant.DISTANCE_OFFSET) {
                placeName = place.getName();
                break;
            }
        }
        // test place
        placeName = "HOME";
        timestamp = System.currentTimeMillis();

        sendNotificationToCareGivers(eventType, notification, placeName, timestamp);

        if (!notification.isEmpty()) {
            Intent i = new Intent();
            i.putExtra("place", placeName);
            i.putExtra("notification", notification);
            i.putExtra("time", timestamp);
            i.setAction(Constant.BROADCAST_NOTIFICATION);
            LocalBroadcastManager.getInstance(this).sendBroadcast(i);
        }
    }

    public void sendNotificationToCareGivers(String eventType, String message, String place, long timestamp) {
        List<String> tokens = new ArrayList<>();
        String referralCode = SharedPrefUtil.loadString(this, Constant.PREF_USER_REFERRAL, "");
        AndroidNetworking.get(API.GET_CAREGIVERS)
                .addQueryParameter("referralCode", referralCode)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Gson gson = new Gson();
                            for (int i = 0; i < response.length(); i++) {
                                String item = response.getString(i);
                                UserModel model = gson.fromJson(item, UserModel.class);
                                tokens.add(model.getPush_token());
                            }
                            JSONObject data = new JSONObject();
                            data.put(Constant.PUSH_TYPE, eventType);
                            data.put("place", place);
                            data.put("timestamp", timestamp);
                            data.put("notification", message);
                            PostNotification.send(message, data, tokens);
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }

    public static String getAddressForLocation(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(ShareCare.SharedApplication, Locale.getDefault());

        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(
                    latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                if (address.getFeatureName() != null)
                    result += address.getFeatureName() + " ";
                if (address.getPostalCode() != null) {
                    result += address.getPostalCode() + " ";
                }
                if (address.getLocality() != null) {
                    result = address.getLocality() + " ";
                }
                if (address.getCountryName() != null)
                    result += address.getCountryName();
            }
        } catch (IOException e) {
        }
        return result;
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }
}
