package project.labs.avviotech.com.sharecare.onesignal;

/**
 * Created by NJX on 3/16/2018.
 */

import android.content.Intent;

import com.onesignal.OSNotification;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.activities.MainActivity;

/**
 * Created by androidbash on 12/14/2016.
 */

//This will be called when a notification is received while your app is running.
public class MyNotificationReceivedHandler  implements OneSignal.NotificationReceivedHandler {
    @Override
    public void notificationReceived(OSNotification notification) {
//        JSONObject data = notification.payload.additionalData;
//        if (data != null) {
//
//            Intent intent = new Intent(ShareCare.SharedApplication, MainActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
//
////            notiType = data.optString(Constant.PUSH_TYPE, null);
////            if (notiType.equals(Constant.PUSH_TYPE_SHARE)) {
//            intent.putExtra("notiData", data.toString());
////            }
//
//            ShareCare.SharedApplication.startActivity(intent);
//
//
//        }
    }
}
