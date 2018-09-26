package project.labs.avviotech.com.sharecare.onesignal;

/**
 * Created by NJX on 3/16/2018.
 */
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.onesignal.NotificationExtenderService;
import com.onesignal.OSNotificationDisplayedResult;
import com.onesignal.OSNotificationReceivedResult;

import org.json.JSONObject;

import java.math.BigInteger;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.activities.MainActivity;

/**
 * Created by androidbash on 12/14/2016.
 */

public class MyNotificationExtenderService extends NotificationExtenderService {
    @Override
    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
        JSONObject data = receivedResult.payload.additionalData;
        if (data != null) {

            Intent intent = new Intent(ShareCare.SharedApplication, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("notiData", data.toString());
            Log.e("notiData==1", data.toString());
            ShareCare.SharedApplication.startActivity(intent);

        }

        return true;
    }
}