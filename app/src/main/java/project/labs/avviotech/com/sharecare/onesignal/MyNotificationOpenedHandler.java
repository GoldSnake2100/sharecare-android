package project.labs.avviotech.com.sharecare.onesignal;

/**
 * Created by NJX on 3/16/2018.
 */

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONObject;

import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.activities.MainActivity;
import project.labs.avviotech.com.sharecare.activities.SplashActivity;
import project.labs.avviotech.com.sharecare.utils.Constant;

/**
 * Created by androidbash on 12/14/2016.
 */

public class MyNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        OSNotificationAction.ActionType actionType = result.action.type;
        JSONObject data = result.notification.payload.additionalData;
        String notiType;

        //While sending a Push notification from OneSignal dashboard
        // you can send an addtional data named "activityToBeOpened" and retrieve the value of it and do necessary operation
        //If key is "activityToBeOpened" and value is "AnotherActivity", then when a user clicks
        //on the notification, AnotherActivity will be opened.
        //Else, if we have not set any additional data MainActivity is opened.


        //If we send notification with action buttons we need to specidy the button id's and retrieve it to
        //do the necessary operation.
        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);
            if (result.action.actionID.equals("ActionOne")) {
                Toast.makeText(ShareCare.SharedApplication, "ActionOne Button was pressed", Toast.LENGTH_LONG).show();
            } else if (result.action.actionID.equals("ActionTwo")) {
                Toast.makeText(ShareCare.SharedApplication, "ActionTwo Button was pressed", Toast.LENGTH_LONG).show();
            }
        }
    }
}