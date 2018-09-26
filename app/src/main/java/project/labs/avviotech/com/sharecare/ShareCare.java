package project.labs.avviotech.com.sharecare;

import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AlertDialog;

import com.androidnetworking.AndroidNetworking;
import com.onesignal.OSSubscriptionObserver;
import com.onesignal.OSSubscriptionStateChanges;
import com.onesignal.OneSignal;
import com.polidea.rxandroidble.RxBleClient;
import com.polidea.rxandroidble.internal.RxBleLog;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import project.labs.avviotech.com.sharecare.bt.BTManager;
import project.labs.avviotech.com.sharecare.bt.RSSIManager;
import project.labs.avviotech.com.sharecare.onesignal.MyNotificationOpenedHandler;
import project.labs.avviotech.com.sharecare.onesignal.MyNotificationReceivedHandler;
import project.labs.avviotech.com.sharecare.utils.Constant;
import project.labs.avviotech.com.sharecare.utils.DBHelper;
import project.labs.avviotech.com.sharecare.utils.SharedPrefUtil;

/**
 * Created by NJX on 3/11/2018.
 */

public class ShareCare extends Application implements OSSubscriptionObserver {

    public static ShareCare SharedApplication;
    public static ArrayList<Handler> remoteHandlers;
    public static Context context;
    public static BTManager btManager;
    public static RSSIManager rssiManager;
    public static DBHelper dbHelper;
    private RxBleClient rxBleClient;

    @Override
    public void onCreate() {
        super.onCreate();
        SharedApplication = this;
        OkHttpClient okHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                . writeTimeout(120, TimeUnit.SECONDS)
                .build();
        AndroidNetworking.initialize(getApplicationContext(),okHttpClient);
        AndroidNetworking.enableLogging();

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new MyNotificationOpenedHandler())
                .setNotificationReceivedHandler( new MyNotificationReceivedHandler() )
//                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.addSubscriptionObserver(this);

        btManager = new BTManager();
        rssiManager = new RSSIManager();
        remoteHandlers = new ArrayList<>();
        rxBleClient = RxBleClient.create(this);
        RxBleClient.setLogLevel(RxBleLog.DEBUG);

        dbHelper = new DBHelper(this);
    }

    public static ArrayList<Handler> getRemoteHandlers() {
        return remoteHandlers;
    }

    public static void setRemoteHandlers(ArrayList<Handler> remoteHandlers) {
        SharedApplication.remoteHandlers = remoteHandlers;
    }

    public static void setContext(Context context) {
        SharedApplication.context = context;
    }

    public static BTManager getBtManager() {
        return btManager;
    }

    public static void setBtManager(BTManager btManager) {
        SharedApplication.btManager = btManager;
    }

    public static RxBleClient getRxBleClient() {
        return SharedApplication.rxBleClient;
    }

    public void setRxBleClient(RxBleClient rxBleClient) {
        this.rxBleClient = rxBleClient;
    }

    public static RSSIManager getRssiManager() {
        return rssiManager;
    }

    public static void setRssiManager(RSSIManager rssiManager) {
        SharedApplication.rssiManager = rssiManager;
    }

    public static void registerRemoteHandler(Handler h)
    {
        remoteHandlers.add(h);
    }

    @Override
    public void onOSSubscriptionChanged(OSSubscriptionStateChanges stateChanges) {
        if (!stateChanges.getFrom().getSubscribed() &&
                stateChanges.getTo().getSubscribed()) {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.push_subscribe)
                    .show();
            // get player ID
            String token = stateChanges.getTo().getPushToken();
            SharedPrefUtil.saveString(this, Constant.PREF_PUSH_TOKEN, token);
        }
    }
}
