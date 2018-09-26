package project.labs.avviotech.com.sharecare.utils;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by NJX on 3/17/2018.
 */

public class CommonUtil {

    public static int getDistance(LatLng firstLatLng, LatLng secLatLng) {
        float distance;
        if (firstLatLng == null || secLatLng == null)
            return Integer.MAX_VALUE;
        Location first = new Location("");
        Location seconde = new Location("");
        first.setLatitude(firstLatLng.latitude);
        first.setLongitude(firstLatLng.longitude);
        seconde.setLatitude(secLatLng.latitude);
        seconde.setLongitude(secLatLng.longitude);
        distance = first.distanceTo(seconde);
        return (int) distance;
    }

    public static void sendSMToFriend(Context context, String phoneNumber) {
        try {
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO);
            smsIntent.addCategory(Intent.CATEGORY_DEFAULT);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.setData(Uri.parse("sms:" + phoneNumber));
            context.startActivity(smsIntent);
        }
        catch (Exception e)
        {
            Toast.makeText(context, "Message not Sent" + e.toString(),
                    Toast.LENGTH_LONG).show();
        }

    }

    public static void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent successfully",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context,ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }

    public static String convertDateWithTimeStamp(String time) {
        DateFormat format = new SimpleDateFormat("h:mm aa yyyy/dd/MM");
        return format.format(new Date(Long.valueOf(time)));
    }

    public static boolean validateEmail(String emailStr) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }

    public static boolean validatePhonNumber(String phoneStr) {
        Pattern VALID_PHONE_NUMBER_REGEX =
                Pattern.compile("^[0-9\\-]*$");
        Matcher matcher = VALID_PHONE_NUMBER_REGEX.matcher(phoneStr);
        return matcher.find();
    }
}
