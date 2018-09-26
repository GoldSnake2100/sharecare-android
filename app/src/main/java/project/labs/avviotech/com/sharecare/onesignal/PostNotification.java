package project.labs.avviotech.com.sharecare.onesignal;

import android.util.Log;

import com.onesignal.OneSignal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by NJX on 3/16/2018.
 */

public class PostNotification {

    public static void send(String message, JSONObject data, List<String> userIds) {
        JSONObject param = new JSONObject();
        try {
            JSONObject contents = new JSONObject();
            contents.put("en", message);
            JSONArray arrIds = new JSONArray(userIds);

            param.put("contents", contents);
            param.put("include_player_ids", arrIds);
            param.put("data", data);
            OneSignal.postNotification(param,
                    new OneSignal.PostNotificationResponseHandler() {
                        @Override
                        public void onSuccess(JSONObject response) {
                            Log.i("OneSignalExample", "postNotification Success: " + response.toString());
                        }

                        @Override
                        public void onFailure(JSONObject response) {
                            Log.e("OneSignalExample", "postNotification Failure: " + response.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
