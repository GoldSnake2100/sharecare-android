package project.labs.avviotech.com.sharecare.utils;

import android.os.Environment;

import java.io.File;

/**
 * Created by NJX on 3/11/2018.
 */

public class Constant {

    public static final String PREF_PUSH_TOKEN = "push_token";
    public static String PREF_AUTOLOGIN = "pref_autologin";

    public static String PREF_USER_ID = "pref_user_id";
    public static String PREF_USER_EMAIL = "pref_user_email";
    public static String PREF_USER_NAME = "pref_user_name";
    public static String PREF_USER_IMAGE = "pref_user_image";
    public static String PREF_USER_REFERRAL = "pref_user_referral";

    public static String PREF_AUTO_CONNECT = "pref_auto_connect";
    public static String PREF_LOCATION_ENABLE = "pref_location_enable";

    // push notification
    public static String PUSH_TYPE = "notiType";
    public static String PUSH_TYPE_SHARE = "notiTypeShare";
    public static String PUSH_TYPE_ACCEPT = "notiTypeAccept";
    public static String PUSH_TYPE_REJECT = "notiTypeReject";
    public static String PUSH_TYPE_INVITE = "notiTypeInvite";
    public static String PUSH_TYPE_CHILD_EVENT_ON = "notiTypeChildEventOn";
    public static String PUSH_TYPE_CHILD_EVENT_OFF = "notiTypeChildEventOff";

    // broadcast
    public static String BROADCAST_LOCATION_STATE = "location_state";
    public static String BROADCAST_NOTIFICATION = "notification";

    public static final String DIR_LOCAL_IMAGE = Environment.getExternalStorageDirectory() + File.separator + "ShareCare";
    public static final String DIR_LOCAL_IMAGE_TEMP = DIR_LOCAL_IMAGE + File.separator + "temp";
    public static final int IMAGE_MAX_SIZE = 851;

    public static int DISTANCE_OFFSET = 500;

}
