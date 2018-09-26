package project.labs.avviotech.com.sharecare.utils;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import project.labs.avviotech.com.sharecare.R;
import project.labs.avviotech.com.sharecare.ShareCare;
import project.labs.avviotech.com.sharecare.bt.ConnectionManager;
import project.labs.avviotech.com.sharecare.models.BTDevice;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;
import static android.content.Context.ACTIVITY_SERVICE;

@SuppressWarnings({"unused", "RedundantIfStatement", "ConstantConditions", "deprecation", "RedundantStringToString", "AccessStaticViaInstance"})
public class Util {
    public static final String name = "project.labs.avviotech.com.sharecare";
    public static final String ACTIVATE = "user/activateacc";
    public static final String VIDEO_CALL = "user/first/call";
    public static final String SECOND_CALL = "user/second/call";
    public static final String END_CALL = "user/end/call";
    public static final String END_PRIVATE_CALL = "user/end/privatecall";
    public static final String SAVE_DISCOVER = "user/broadcast/viewer/save";
    public static final String LEAVE_BRAOADCAST = "user/broadcast/leave";
    public static final String FIREBASE_URL = "https://instance-1fa99.firebaseio.com/";
    static final String FIREBASE_STORAGE_URL = "gs://instance-1fa99.appspot.com";
    public static final String ACCEPT_PRIVATE_INVITE = "user/private/call/initiate";
    public static final String GET_END_BROAD = "user/broadcast";
    public static String SEND_BROADCAST_INVITE_PRIVATE = "user/private/call/invite";
    public static String AUTO_DECLINE_PRIVATE_CALL = "user/private/call/leave";
    public static String PACKAGE_NAME = "io.app.instance";
    public static String PACKAGE_NAME_WITH_ACTIVITY = "io.app.instance.activity";
    public static String BASE_URL = "http://50.63.13.121:8082/public/rest/";
    public static String BASE_CHAT_URL = "http://50.63.13.121:8080/spika/v1//";
    public static String CHAT_URL = "http://50.63.13.121:8080/spika";
    public static String REGISTER = "users/insertUser/";
    public static String REPORT_API = "user/broadcast/report";
    public static String USER_CREDIT_ACCOUNT = "user/creditaccount";
    public static String LOGIN = "users/getUser";
    public static String USER_CONTACT_IMPORT = "usercontacts/getByUserId";
    public static String USER_CHAT_ROOM = "userrooms/getByUserId";
    public static String USER_CHAT_ROOMS = "userrooms/getUserRooms";
    public static String USER_UPDATE_CHAT_ROOMS = "userrooms/updateRoomData";
    public static String USER_UPDATE_ROOMS = "userrooms/updateRoom";
    public static String USER_UPDATE_DEVICE_TOKEN = "userdevices/updateDeviceToken";
    public static String USER_UPDATE_USER = "users/updateUser";
    public static String USER_UPDATE_USER_PASWORD = "users/updateUserPassword";
    public static String CREATE_CHAT_ROOM = "userrooms/createUserRoom";
    public static String SEND_INVITATION = "user/contact/invite";

    public static String FETCH_CONTACTS = "user/contacts/?is_blocked=0";
    public static String FETCH_CONTACTS_WITH_BLOCKED = "user/contacts/?is_blocked=1";
    public static String SEND_BROADCAST_INVITE = "user/broadcast/invite";
    public static String CALL_BROADCAST_TOGETHER_INVITE = "user/broadcast/invite/together";
    public static String FETCH_BROADCAST_VIEWER = "user/broadcast/viewer/active?broadcast_id=";
    public static String FETCH_PRIVATE_BROADCAST_VIEWER = "/user/private/callers/?broadcast_id=";
    public static String REQUEST_PAYMENT = "user/requestPayments";
    public static String MAKE_PAYMENT = "user/makepayment";
    public static String END_BROADCAST = "user/broadcast/end/";
    public static String DISCOVER_END_BROADCAST = "user/broadcast/";
    public static String UPDATE_BROADCAST = "user/broadcast/together/";
    public static String INVITEE_BROADCASTER = "invitee_broadcaster";
    public static String FETCH_FOLLOWERS_LIST = "user/follow/?follow_option=1";
    public static String USER_LIVE_BROADCAST = "user/discover/?is_live=1";
    public static String USER_TRENDING_BROADCAST = "user/discover/?is_trending=1";
    public static String USER_CATEGORY_BROADCAST = "user/discover/?category_id=";
    public static String USER_BROADCAST_PUBLIC = "user/broadcast/?broadcast_id=";
    public static String USER_LIKE = "user/broadcast/like";
    public static String USER_POST_LIKE = "user/post/like";
    public static String USER_COMMENT = "user/broadcast/comment";
    public static String USER_POST_COMMENT = "user/post/comment";
    public static String CREATE_BROADCAST = "user/broadcast";
    public static String FETCH_USER_FOLLOWER = "user/follow/?follow_option=2";
    public static String BLOCK_USER = "user/contact/block/";
    public static String USER_CONTACT = "user/contacts";
    public static String VERIFY_OTP = "user/verifyotp";
    public static String REGEN_OTP = "user/regenotp";
    public static String MEMBERSHIP_TYPES = "admin/membership/";
    public static String GET_ALL_PAYMENT_HISTORY = "user/payment/";
    public static String RESET_PASSWORD = "user/mailkey";
    public static String ENTERPRISE_LOGIN = "user/enterpriselogin";
    public static String FOLLOWERS = "user/follow/?follow_option=2";
    public static String GET_FLLOWING = "user/follow/?follow_option=1";
    public static String USER_INBOX = "user/inbox";
    public static String USER_MESSAGE_READ = "user/message/read/";
    public static String USER_NOTIFICATIONS = "user/notifications";
    public static String USER_CATEGORIES = "admin/categories";
    public static String USER_Logout = "/user/logout";
    public static String USER_REQUEST = "user/notification/request";
    public static String CHNAGE_PWD = "user/changepwd";
    public static String USER_PRIVACY = "user/privacy";
    public static String CONTACTS_WITH_SEARCH = "user/contacts";
    public static String BLOCKED_LIST = "user/contacts/";
    public static String ENTERPRISE_ASSOCIATE = "user/enterprise/associate";
    public static String REVIEW_COMMENTS = "user/review";
    public static String USER_PAYMENT_METHOD = "user/paymentmethod";
    public static String USER_CALL_SETTING = "user/callsetting";
    public static String USER_PAYMENT = "user/paymentmethod";
    public static String SEND_MESSAGE = "user/sendmessage";
    public static String SAVE_POST = "user/post";
    public static String SAVE_POST_BC = "user/broadcast/post";
    public static String USER_CHAT_STATUS = "user/chat/status";

    public static String GET_PROFILE = "user/single/";
    public static String USER_POST_TYPE = "user/post/type";
    public static String UPDATE_PROFILE = "user/profile";
    public static String USER_OTHER_PROFILE = "users/getByUserId";
    public static String GET_NOTIFICATIONS = "user/notification/setting";
    public static String GET_COUNTRIES = "admin/countries";
    public static String POST_FEEBACK = "user/feedback";
    public static String SOCIAL_LOGIN = "user/sociallogin";
    public static String SAVE_VIDEO = "user/video";
    public static String GET_VIDEOS = "user/video/?need_all=1";
    public static final String API_KEY = "API_KEY";
    public static final String BROAD_CAST_MESSAGE = "broad_cast_message";

    public static String POST_SETTINGS = "user/base/setting";
    public static String PUT_MESSAGE = "user/broadcast/message";
    public static String POST_PROFILE_UPLOAD_IMAGE_VIDEO = "user/profile/pic";
    public static String FOLLOW = "user/follow";
    public static String CONTACT_INVITE = "user/contact/invite";

    public static String CONTACT_UNFRIEND = "user/contact/";
    public static String USER_REPORT = "user/report";

    public static final String DEVICE_TOKEN = "DEVICE_TOKEN";
    public static final String USER_ID = "USER_ID";
    public static final String PUT_METHOD = "PUT";
    public static final String GET_METHOD = "GET";
    public static final String POST_METHOD = "POST";
    public static final String DELETE_METHOD = "DELETE";
    public static final String NAME = "NAME";
    public static final String PROFILE_THUMB = "PROFILE_THUMB";
    public static final String EMAIL = "EMAIL";
    public static final String WEB = "WEB";
    public static final String IS_LOGIN = "IS_LOGIN";
    public static final String COINS_COUNT = "COUNS_COUNT";
    public static final String FOLLOW_COUNT = "FOLLOW_COUNT";
    public static final String LIKE_COUNT = "LIKE_COUNT";
    public static final String DOB = "DOB";
    public static final String GENDER = "GENDER";
    public static final String COUNTRY = "COUNTRY";
    public static final String USER_TYPE = "user_type";
    public static final String MOBILE_NUMBER = "mobile_number";
    public static final String MAILC_OUNT = "mail_count";
    public static final String whomIamFollowing = "whomIamFollowing";
    public static final String discoverFollowers = "discoverFollowers";
    public static String IS_LOGIN_VIA_SOCIAL = "login_via_social";
    public static String IS_PROFILE_IMAGE = "is_profile_image";
    public static String IS_PROFILE_VIDEO = "is_profile_video";

    public static final String MEMBER_FROM = "member_from";
    public static final String NICK_NAME = "nick_name";
    public static String selectedCountry;
    public static String selectedCountryId;

    public static int trendSelected = 3;
    public static String USER_API_KEY;
    public static String USER_USER_ID;
    //"04/03/2014 23:41:37",
    private static SimpleDateFormat mMachineSdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);

    private static SimpleDateFormat mHumanSdf = new SimpleDateFormat("MM/dd/yyyy hh:mm a", Locale.US);

    public static ArrayList<String> multiSelectedList = new ArrayList<>();

    static {
        mMachineSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    public static boolean isKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }

    public static String getHumanDateString() {
        return mHumanSdf.format(new Date());
    }

    public static String getHumanRelativeDateStringFromString(String machineDateStr) {
        String result = null;
        try {
            result = DateUtils.getRelativeTimeSpanString(mMachineSdf.parse(machineDateStr).getTime()).toString();
            result = result.replace("in 0 minutes", "just now");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }





    public static void setServerUrlToImageView(Activity activity, ImageView imageView, String url) {
        if (url != null) {
            if (URLUtil.isValidUrl(url)) {
                Glide.with(activity).load(url).into(imageView);
            } else {
                System.err.println("InValid in url " + url);
            }
        } else {
            System.err.println("Problem in url " + url);
        }
    }

    public static void setServerUrlToImageViewWithSize(Activity activity, ImageView imageView, String url, int width, int height) {
        if (url != null) {
            Glide.with(activity)
                    .load(url)
                    .apply(new RequestOptions().override(width, height))// Uri of the picture
                    .into(imageView);
        }
    }

    public static void setRoundImageWithSize(Activity activity, ImageView imageView, String url, int width, int height) {
        if (url != null) {
            Glide.with(activity)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform())
                    .into(imageView);
        }
    }

    public static void setRoundImageWithContext(Context activity, ImageView imageView, String url, int width, int height) {
        if (url != null) {
            Glide.with(activity)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform().override(width, height))
                    .into(imageView);
        }
    }

    public static boolean verifyEditText(EditText editText) {
        if (editText.getText().toString().toString() == null || editText.getText().toString().isEmpty() || "".equalsIgnoreCase(editText.getText().toString().trim())) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean verifyPassword(EditText contactNumberEditText) {
        if (contactNumberEditText.getText().length() >= 4) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean verifyContactNumber(EditText editText) {
        if (editText.getText().toString().matches("[0-9]+") && editText.getText().toString().length() == 10) {
            return false;
        } else {
            return true;
        }
    }

    public static void showInternetNotAvailable(Activity activity) {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(activity).create();
        final View alertView = View.inflate(activity, R.layout.common_dialog_box, null);
        TextView alertBody = (TextView) alertView.findViewById(R.id.common_alert_body);
        SharedPreferences languagePreferences = activity.getSharedPreferences("language_info", activity.MODE_PRIVATE);
        alertBody.setText(languagePreferences.getString("internet_not_available", "Internet is not Available, please try later"));
        TextView alertOk = (TextView) alertView.findViewById(R.id.common_alert_ok);
        alertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(alertView, 0, 0, 0, 0);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.show();
    }

    public static void showInternetNotAvailable(Context activity) {
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(activity).create();
        final View alertView = View.inflate(activity, R.layout.common_dialog_box, null);
        TextView alertBody = (TextView) alertView.findViewById(R.id.common_alert_body);
        SharedPreferences languagePreferences = activity.getSharedPreferences("language_info", activity.MODE_PRIVATE);
        alertBody.setText(languagePreferences.getString("internet_not_available", "Internet is not Available, please try later"));
        TextView alertOk = (TextView) alertView.findViewById(R.id.common_alert_ok);
        alertOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(alertView, 0, 0, 0, 0);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
        alertDialog.show();
    }

    public static void commonAlertDialog(Activity activity, String body, boolean cancellable) {

        try {
            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(activity).create();
            alertDialog.setCancelable(cancellable);
            alertDialog.setCanceledOnTouchOutside(cancellable);
            final View alertView = View.inflate(activity, R.layout.common_dialog_box, null);
            TextView alertBody = (TextView) alertView.findViewById(R.id.common_alert_body);
            alertBody.setText(body);
            TextView alertOk = (TextView) alertView.findViewById(R.id.common_alert_ok);
            alertOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.setView(alertView, 0, 0, 0, 0);
            alertDialog.getWindow().getAttributes().windowAnimations = R.style.PauseDialogAnimation;
            alertDialog.show();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

    }


     public static ArrayList<String> getCountriesList() {
        Locale[] locales = Locale.getAvailableLocales();
        ArrayList<String> countries = new ArrayList<>();
        for (Locale locale : locales) {
            String country = locale.getDisplayCountry();
            if (country.trim().length() > 0 && !countries.contains(country)) {
                countries.add(country);
            }
        }
        Collections.sort(countries);
        return countries;
    }

    public static void hideKeypad(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeypad(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    public static void setImageToImageViewWithSize(Activity activity, ImageView imageView, String url, int width, int height) {
        if (url != null) {
            Glide.with(activity)
                    .load(new File(url))// Uri of the picture
                    .apply(new RequestOptions().override(width, height))
                    .into(imageView);
        }
    }

    public static void setRoundServerUrlToImageViewWithSize(Activity activity, ImageView imageView, String url, int width, int height) {
        if (url != null) {
            Glide.with(activity)
                    .load(url)
                    .apply(RequestOptions.circleCropTransform().override(width, height))
                    .into(imageView);
        }
    }

    public static boolean checkForExtensionMatching(String extension) {
        if ("gif".equalsIgnoreCase(extension))
            return true;
        else if ("jpg".equalsIgnoreCase(extension))
            return true;
        else if ("png".equalsIgnoreCase(extension))
            return true;
        else
            return false;
    }

    public static ArrayList<String> getetMonths() {
        ArrayList<String> months = new ArrayList<>();
        months.add("Jan");
        months.add("Feb");
        months.add("Mar");
        months.add("Apr");
        months.add("May");
        months.add("Jun");
        months.add("Jul");
        months.add("Aug");
        months.add("Sep");
        months.add("Oct");
        months.add("Nov");
        months.add("Dec");

        return months;
    }

    public static boolean isAppInForeground(Context context)
    {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
            ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
            String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

            return foregroundTaskPackageName.toLowerCase().equals(context.getPackageName().toLowerCase());
        }
        else
        {
            ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
            ActivityManager.getMyMemoryState(appProcessInfo);
            if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE)
            {
                return true;
            }

            KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            // App is foreground, but screen is locked, so show notification
            return km.inKeyguardRestrictedInputMode();
        }
    }

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }




    private static void waitForNotification(Activity ac) {

        SharedPreferences prefs = project.labs.avviotech.com.sharecare.utils.PreferenceManager.getDefaultSharedPreferences(ac);
        SharedPreferences.Editor edit = prefs.edit();
        edit.putBoolean("waitingForNotification", true);
        edit.apply();


    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED
                || connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTING) {
            return true;
        } else if (connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                || connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTING) {
            return true;
        } else
            return false;
    }

    private static NetworkInfo getNetworkInfo(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static boolean isConnectedFast(Context context){
        NetworkInfo info = getNetworkInfo(context);
        return (info != null && info.isConnected() && isConnectionFast(info.getType(),info.getSubtype()));
    }

    private static boolean isConnectionFast(int type, int subType){
        if(type== ConnectivityManager.TYPE_WIFI){
            return true;
        }else if(type== ConnectivityManager.TYPE_MOBILE){
            switch(subType){
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    return false; // ~ 14-64 kbps
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return false; // ~ 50-100 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    return true; // ~ 400-1000 kbps
                case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    return true; // ~ 600-1400 kbps
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return false; // ~ 100 kbps
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return true; // ~ 2-14 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPA:
                    return true; // ~ 700-1700 kbps
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return true; // ~ 1-23 Mbps
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return true; // ~ 400-7000 kbps
			/*
			 * Above API level 7, make sure to set android:targetSdkVersion
			 * to appropriate level to use these
			 */
                case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
                    return true; // ~ 1-2 Mbps
                case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9
                    return true; // ~ 5 Mbps
                case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                    return true; // ~ 10-20 Mbps
                case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
                    return false; // ~25 kbps
                case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                    return true; // ~ 10+ Mbps
                // Unknown
                case TelephonyManager.NETWORK_TYPE_UNKNOWN:
                default:
                    return false;
            }
        }else{
            return false;
        }
    }


    public static String createUniqueID(String name, String id)
    {
        name = name.replaceAll(" ","_");
        String uniqueId = name + "_" + id + "_" + new Date().getTime();
        return uniqueId;
    }

    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("QChat", "error " + e.getMessage());
        }
        return apiKey;
    }

    private static MediaPlayer mMediaPlayer;
    public static void enableBluetooth()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothAdapter.enable();
    }

    public static boolean checkBluetoothStatus()
    {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        return mBluetoothAdapter.isEnabled();
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context)
    {
        String imei = "";

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        imei = telephonyManager.getDeviceId();

        return  imei;
    }


    public static String getVersion(Context context)
    {
        String version = "";

        try
        {
            PackageManager pm = context.getPackageManager();
            PackageInfo info1 = pm.getPackageInfo(context.getPackageName(), 0);
            version = info1.versionName;
        }catch(Exception e)
        {
            e.printStackTrace();
        }

        return version;
    }
    @SuppressLint("MissingPermission")
    public static String getMobileTowerDATA(Context context)
    {
        String mobileData = "";

        TelephonyManager tManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);

        try {
            if (tManager.getAllCellInfo() != null && tManager.getAllCellInfo().size() > 0) {
                for (int i = 0; i < tManager.getAllCellInfo().size(); i++) {
                    CellInfo info = tManager.getAllCellInfo().get(i);
                    if (info != null) {
                        if (info instanceof CellInfoGsm) {
                            CellInfoGsm cellinfogsm = (CellInfoGsm) info;
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                            if (cellSignalStrengthGsm != null && cellinfogsm.getCellIdentity() != null) {
                                int cid = cellinfogsm.getCellIdentity().getCid();
                                int lac = cellinfogsm.getCellIdentity().getLac();
                                int mcc = cellinfogsm.getCellIdentity().getMcc();
                                int mnc = cellinfogsm.getCellIdentity().getMnc();
                                int rxl = cellSignalStrengthGsm.getDbm();
                                mobileData = "" + mcc + "," + mnc + "," + lac + "," + cid + "," + rxl;
                            }
                        } else if (info instanceof CellInfoWcdma) {
                            CellInfoWcdma cellinfogsm = (CellInfoWcdma) info;
                            CellSignalStrengthWcdma cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                            if (cellSignalStrengthGsm != null && cellinfogsm.getCellIdentity() != null) {
                                int cid = cellinfogsm.getCellIdentity().getCid();
                                int lac = cellinfogsm.getCellIdentity().getLac();
                                int mcc = cellinfogsm.getCellIdentity().getMcc();
                                int mnc = cellinfogsm.getCellIdentity().getMnc();
                                int rxl = cellSignalStrengthGsm.getLevel();
                                mobileData = "" + mcc + "," + mnc + "," + lac + "," + cid + "," + rxl;
                            }
                        }
                    }
                }


            }
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return mobileData;
    }

    public static float getBatteryLevel(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        // Error checking that probably isn't needed but I added just in case.
        if(level == -1 || scale == -1) {
            return 50.0f;
        }

        return ((float)level / (float)scale) * 100.0f;
    }

    public static int isBatteryCharging(Context context) {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = false;

        if(status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL)
            isCharging = true;


        if(isCharging)
            return 1;

        return 0;
    }



    public static String getDeivceName(String address)
    {
        if(Constants.deviceMap != null && Constants.deviceMap.get(address) != null)
            return Constants.deviceMap.get(address).getName();

        return "";
    }
    public static boolean isDeviceConnected(String name)
    {
        if(Constants.deviceMap != null)
        {
            for (Map.Entry<String, BTDevice> entry : Constants.deviceMap.entrySet()) {
                if(Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(entry.getKey()) != null)
                {
                    ConnectionManager manager = Constants.connectionManagerMap.get(entry.getKey());
                    if(Constants.deviceMap != null && Constants.deviceMap.get(entry.getKey()) != null)
                    {
                        BTDevice device = Constants.deviceMap.get(entry.getKey());

                        String devName = device.getName();

                        if(devName.indexOf(name) != -1)
                        {
                            if(manager.isConnected())
                                return true;
                        }
                    }


                }
            }
        }
        return false;
    }

    public static void powerOffDevice(String address)
    {
        if(Constants.deviceMap != null)
        {
            if(Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(address) != null)
            {
                ConnectionManager manager = Constants.connectionManagerMap.get(address);
                manager.powerOffDevice();
            }
        }

    }

    public static ConnectionManager getConnectionManager(String address)
    {
        ConnectionManager manager = null;
        if(Constants.deviceMap != null)
        {
            if(Constants.connectionManagerMap != null && Constants.connectionManagerMap.get(address) != null)
            {
                manager = Constants.connectionManagerMap.get(address);
            }
        }

        return manager;

    }

    public static int convertBytetoInt(byte[] bytes) {
        try {
            String hex = bytesToHex(bytes);
            return Integer.parseInt(hex,16);
        } catch (Exception e) {

        }
        return 10;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        byte[] b = new byte[2];
        b[0] = bytes[1];
        b[1] = bytes[0];
        for ( int j = 0; j < b.length; j++ ) {
            int v = b[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public static void displayToast(String message, Context context)
    {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static boolean isBugFinderEnabled()
    {
        return Constants.isBugFinderEnabled;
    }

    public static void playSound(Context context, int duration)
    {
        try
        {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, soundUri);
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(true);
                mMediaPlayer.prepare();

            }

            if (mMediaPlayer != null && !mMediaPlayer.isPlaying())
                mMediaPlayer.start();
            if(duration > 0)
            {
                new CountDownTimer(duration,1000)
                {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        if(mMediaPlayer != null && mMediaPlayer.isPlaying())
                            mMediaPlayer.stop();
                    }
                }.start();
            }


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }
    public static void stopSound(Context context)
    {
        try
        {
            if(mMediaPlayer != null && mMediaPlayer.isPlaying())
                mMediaPlayer.stop();


        }catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static boolean shouldConnect(String address)
    {
        if(ShareCare.rssiManager != null)
        {
            double distance = ShareCare.rssiManager.getDistance(address);
            if(distance < 5)
                return true;
        }
        return false;
    }
    public static boolean shouldDisConnect(String address)
    {
        if(ShareCare.rssiManager != null)
        {
            double distance = ShareCare.rssiManager.getDistance(address);
            if(distance > 5)
                return true;
        }
        return false;
    }
    public static void disconnectAllDevices()
    {
        if(!Util.isDeviceConnected("PETTAG") && !Util.isDeviceConnected("FLEXCLIP"))
        {
            ShareCare.btManager.resetFilters();
            for (Map.Entry<String, ConnectionManager> entry : Constants.connectionManagerMap.entrySet()) {
                String deivceName = Util.getDeivceName(entry.getKey());
                if(deivceName.indexOf("KEYFOB") == -1)
                {
                    if(entry.getValue() != null && entry.getValue().isConnected())
                        entry.getValue().triggerDisconnect();
                }
            }
        }

    }

    public static void enableOnline(Context context, boolean isOnline) {
        WifiManager wifiManager = (WifiManager)context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(isOnline);
    }
}
