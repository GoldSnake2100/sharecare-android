package project.labs.avviotech.com.sharecare.utils;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import project.labs.avviotech.com.sharecare.bt.ConnectionManager;
import project.labs.avviotech.com.sharecare.models.BTDevice;


/**
 * Created by swayamagrawal on 29/07/17.
 */
public class Constants {

    public static final String TAG = "NANIM";

    public static final String GTSE_IP = "65.99.230.68";
    public static final int GTSE_DCS_PORT = 31110;
    public static final int GTSE_COMMAND_PORT = 30205;

    public static final boolean isBugFinderEnabled = true;
    public static final String ACTION_CONNECT = "com.avviotech.labs.nanni.connected";
    public static final String ACTION_DISCONNECT = "com.avviotech.labs.nanni.disconnected";
    public static final String ACTION_READ_RSSI = "com.avviotech.labs.nanni.read_rssi";
    public static final String ACTION_READ = "com.avviotech.labs.nanni.read";
    public static final String ACTION_WRITE = "com.avviotech.labs.nanni.write";
    public static final String ACTION_NOTIFY = "com.avviotech.labs.nanni.notify";

    public static final int HANDLER_ACTION_CONNECT = 1000;
    public static final int HANDLER_ACTION_DISCONNECT = 1001;
    public static final int HANDLER_ACTION_READ_RSSI = 1002;
    public static final int HANDLER_ACTION_READ = 1003;
    public static final int HANDLER_ACTION_WRITE = 1004;
    public static final int HANDLER_ACTION_NOTIFY = 1005;

    public static ConcurrentHashMap<String,BTDevice> deviceMap;
    public static ConcurrentHashMap<String,ConnectionManager> connectionManagerMap;


    public static final int STATUS_CHILD_UNATTENDED_ON    = 0x0F15;   // 3861
    public static final int STATUS_CHILD_UNATTENDED_OFF    = 0x0F16;   // 3862
    public static final int STATUS_CHILD_DISTRESS_ON = 0x3100;
    public static final int STATUS_PET_DISTRESS_ON = 0x3102;
    public static final int STATUS_AIRBAG_DEPL0Y_ON = 0x3104;
    public static final int STATUS_HELP_URGENT_ON = 0x3106;
    public static final int STATUS_HELP_NONURGENT_ON = 0x3108;
    public static final int STATUS_CHILD_DANGER_ON = 0x310A;
    public static final int STATUS_PET_DANGER_ON = 0x310C;
    public static final int STATUS_CHILD_INSECURE_ON = 0x310E;
    public static final int STATUS_CHILD_SAFE_ON = 0x3110;
    public static final int STATUS_PET_SAFE_ON = 0x3112;
    public static final int STATUS_TRUCK_CARGO_VOILATIONS_ON = 0x3138;
    public static final int STATUS_CABIN_CARGO_VOILATIONS_ON = 0x0F27;
    public static final int STATUS_MANUAL_SWITCH_ON = 0x0F27;
    public static final int STATUS_UNSAFE_CABIN_TEMERATURE_ON = 0x36B2;
    public static final int STATUS_UNSAFE_CABIN_TEMERATURE_OFF = 0x0F01;
    public static final int STATUS_TRUCK_CARGO_VOILATIONS_OFF = 0x0F10;
    public static final int STATUS_TRUCK_RELEASE_VOILATIONS_ON = 0x3A9E;
    public static final int STATUS_SAFE_JOURNEY = 0x0F17;
    public static final int STATUS_SAFE_JOURNEY_OFF = 0x0F20;


    public static final int STATUS_CHILD_UNATTENDED_CANCELLED   = 0x0F21;   // 3873
    public static final int STATUS_CHILD_IN_DANGER_CANCELLED   = 0x0F22;   // 3874
    public static final int STATUS_CHILD_IN_DISTRESS_CANCELLED   = 0x0F23;   // 3875
    public static final int STATUS_PET_IN_DANGER_CANCELLED   = 0x0F24;   // 3876
    public static final int STATUS_PET_IN_DISTRESS_CANCELLED   = 0x0F25;   // 3877


    public static final int STATUS_CO_WARNING_OFF = 0x0F02;
    public static final int STATUS_CO_DANGER_OFF = 0x0F03;
    public static final int STATUS_CO_WARNING = 0x36B0;
    public static final int STATUS_CO_DANGER = 0x36BA;
    public static final int STATUS_PIR_BROKEN = 0x0C01;
    public static final int STATUS_THE_BROKEN = 0x0C02;
    public static final int STATUS_CO2_BROKEN = 0x0C03;
    public static final int STATUS_TEMP_BROKEN = 0x0C04;
    public static final int STATUS_CO_BROKEN = 0x0C05;
    public static final int STATUS_ACM_BROKEN = 0x0C06;

    public static final int STATUS_TRUNK_PIR_BROKEN = 0x0D01;
    public static final int STATUS_TRUNK_THE_BROKEN = 0x0D02;
    public static final int STATUS_TRUNK_CO2_BROKEN = 0x0D03;
    public static final int STATUS_TRUNK_ACM_BROKEN = 0x0D06;


    public static final int STATUS_ACTIVATION_START = 0x2150;

    public static final int STATUS_CHILD_DISTRESS_OFF = 0x3101;
    public static final int STATUS_PET_DISTRESS_OFF = 0x3103;
    public static final int STATUS_AIRBAG_DEPL0Y_OFF = 0x3105;
    public static final int STATUS_HELP_URGENT_OFF = 0x3107;
    public static final int STATUS_HELP_NONURGENT_OFF = 0x3109;
    public static final int STATUS_CHILD_DANGER_OFF = 0x310B;
    public static final int STATUS_PET_DANGER_OFF = 0x310D;
    public static final int STATUS_CHILD_INSECURE_OFF = 0x310F;
    public static final int STATUS_CHILD_SAFE_OFF = 0x3111;
    public static final int STATUS_PET_SAFE_OFF = 0x3113;




    public static final int STATUS_FIRE_ON = 0x3000;
    public static final int STATUS_FIRE_OFF = 0x3001;



    public static final int STATUS_TL501SET_PHONE1_OK = 0x2001;
    public static final int STATUS_TL501SET_PHONE2_OK = 0x2002;
    public static final int STATUS_TL501SET_PHONE3_OK = 0x2003;
    public static final int STATUS_TL501SET_PHONE4_OK = 0x2004;
    public static final int STATUS_TL501SET_PHONE5_OK = 0x2005;
    public static final int STATUS_TL501SET_PHONE6_OK = 0x2006;
    public static final int STATUS_TL501SET_PHONE7_OK = 0x2007;
    public static final int STATUS_TL501SET_PHONE8_OK = 0x2008;
    public static final int STATUS_TL501SET_PHONE_ERR = 0x2009;



    public static final int STATUS_TL501SET_GPRS_OK = 0x2020;
    public static final int STATUS_TL501SET_SINGLE_LOCAL_OK = 0x202A;
    public static final int STATUS_TL501SET_GEO_FENCE_OK = 0x2050;
    public static final int STATUS_TL501SET_SPEED_LIMIT_OK = 0x2070;
    public static final int STATUS_TL501SET_TIMEZONE_OK = 0x2090;
    public static final int STATUS_TL501SET_CONT_TRACK_OK = 0x2095;



    public static final int STATUS_PANIC_ON = 0xF841;
    public static final int STATUS_PANIC_OFF = 0xF842;
    public static final int STATUS_ALARM_ON = 0xF847;
    public static final int STATUS_ALARM_OFF = 0xF848;
    public static final int STATUS_ASSIST_ON = 0xF851;
    public static final int STATUS_ASSIST_OFF = 0xF852;
    public static final int STATUS_MANDOWN_ON = 0xF855;
    public static final int STATUS_MANDOWN_OFF = 0xF856;
    public static final int STATUS_MEDICAL_ON = 0xF861;
    public static final int STATUS_MEDICAL_OFF = 0xF862;



    public static final int STATUS_TOWING_START = 0xF871;
    public static final int STATUS_TOWING_STOP = 0xF872;
    public static final int STATUS_VIBRATION_ON = 0xF891;
    public static final int STATUS_VIBRATION_OFF = 0xF892;



    public static final int STATUS_INTRUSION_ON = 0xF881;
    public static final int STATUS_INTRUSION_OFF = 0xF882;



    public static final int STATUS_EXCESS_BRAKING = 0xF930;
    public static final int STATUS_EXCESS_CORNERING = 0xF937;
    public static final int STATUS_IMPACT = 0xF941;
    public static final int STATUS_FREEFALL = 0xF945;
    public static final int STATUS_EXCESS_ACCEL = 0xF960;




    public static final int STATUS_LOW_FUEL = 0xF954;



    public static final int STATUS_LOW_BATTERY = 0xFD10;
    public static final int STATUS_POWER_FAILURE = 0xFD13;
    public static final int STATUS_POWER_RESTORED = 0xFD15;
    public static final int STATUS_POWER_OFF = 0xFD17;
    public static final int STATUS_POWER_ON = 0xFD19;
    public static final int STATUS_GPS_FAILURE = 0xFD22;
    public static final int STATUS_GPS_RESTORED = 0xFD26;
    public static final int STATUS_HEARTBEAT = 0xF060;
    public static final int STATUS_LOCATION = 0xF020;
    public static final int STATUS_LOCATION1 = 0xF021;
    public static final int STATUS_LOCATION3 = 61475;
    public static final int STATUS_LISTEN = 0xF020;
    public static final int STATUS_GEOFENCE = 0xF270;
    public static final int STATUS_STANDBY = 0xF8B2;
    public static final int STATUS_UPDATE_USER_DATA   = 0x0F26;   // 3878


    public static final HashMap<Integer,String> statusDesc = new HashMap<>();

    static
    {
        statusDesc.put(STATUS_CHILD_UNATTENDED_ON,"Child Unattended On");
        statusDesc.put(STATUS_CHILD_UNATTENDED_OFF,"Child Unattended Off");
        statusDesc.put(STATUS_CHILD_DISTRESS_ON,"Child Distress On");
        statusDesc.put(STATUS_CHILD_DISTRESS_OFF,"Child Distress Off");
        statusDesc.put(STATUS_PET_DISTRESS_ON,"Pet Distress On");
        statusDesc.put(STATUS_PET_DISTRESS_OFF,"Pet Distress Off");
        statusDesc.put(STATUS_PANIC_ON,"Panic On");
        statusDesc.put(STATUS_PANIC_OFF,"Panic Off");
        statusDesc.put(STATUS_CHILD_DANGER_ON,"Child Danger On");
        statusDesc.put(STATUS_CHILD_DANGER_OFF,"Child Danger Off");
        statusDesc.put(STATUS_PET_DANGER_ON,"Pet Danger On");
        statusDesc.put(STATUS_PET_DANGER_OFF,"Pet Danger Off");
        statusDesc.put(STATUS_SAFE_JOURNEY,"Safe Journey On");
        statusDesc.put(STATUS_SAFE_JOURNEY_OFF,"Safe Journey Off");
        statusDesc.put(STATUS_HELP_URGENT_ON,"Help! I am in Urgent Need");
        statusDesc.put(STATUS_HELP_URGENT_OFF,"ECHO ALERT Off");
        statusDesc.put(STATUS_POWER_FAILURE,"Power Interrupt");
        statusDesc.put(STATUS_POWER_RESTORED,"Power Restored");

    }

    // Services
    public static final UUID SVC_GENERIC_ACCESS_PROFILE_UUID = UUID
            .fromString("00001800-0000-1000-8000-00805f9b34fb");

    public static final UUID SVC_DEVICE_INFORMATION_UUID = UUID
            .fromString("0000180A-0000-1000-8000-00805F9B34FB");

    public static final UUID SVC_HEALTH_THERMOMETER_UUID = UUID
            .fromString("00001809-0000-1000-8000-00805f9b34fb");

    public static final UUID SVC_BATTERY_UUID = UUID
            .fromString("0000180F-0000-1000-8000-00805f9b34fb");

    public static final UUID SVC_AUTOMATIONIO_UUID = UUID
            .fromString("00001815-0000-1000-8000-00805f9b34fb");

    public static final UUID SVC_IMA_UUID = UUID
            .fromString("00001802-0000-1000-8000-00805F9B34FB");

    public static final UUID SVC_LINK_LOSS_UUID = UUID
            .fromString("00001803-0000-1000-8000-00805F9B34FB");

    public static final UUID SVC_CUSTOME_SERVICE_UUID = UUID
            .fromString("fe99385c-82e6-11e5-8bcf-feff819cdc9f");

    public static final UUID SVC_BIOSENSOR_SERVICE_UUID = UUID
            .fromString("0000fff0-0000-1000-8000-00805f9b34fb");

    public static final UUID SVC_MCD_UUID = UUID
            .fromString("fe99385c-82e6-11e5-8bcf-feff819cdc9f");

    // Characterstics

    public static final UUID CHAR_MCD_CHAR6_UUID = UUID
            .fromString("00002A00-0000-1000-8000-00805F9B34FB");

    public static final UUID CHAR_MCD_CHAR7_UUID = UUID
            .fromString("00002A00-0000-1000-8000-00805F9B34FB");

    public static final UUID CHAR_DEVICE_NAME_UUID = UUID
            .fromString("00002A00-0000-1000-8000-00805F9B34FB");

    public static final UUID CHAR_MANUFATURE_NAME_STRING_UUID = UUID
            .fromString("00002A29-0000-1000-8000-00805F9B34FB");

    public static final UUID CHAR_BATTERY_LEVEL_UUID = UUID
            .fromString("00002A19-0000-1000-8000-00805f9b34fb");

    public static final UUID CHAR_HTP_TEMPERATURE_MEASUREMENT = UUID
            .fromString("00002A1C-0000-1000-8000-00805f9b34fb");


    public static final UUID CHAR_AUTOMATION_IO = UUID
            .fromString("00002A56-0000-1000-8000-00805f9b34fb");

    public static final UUID CHAR_ALERT_LEVEL_UUID = UUID
            .fromString("00002a06-0000-1000-8000-00805f9b34fb");

    public static final UUID CHAR_CUSTOM_SERVICE_UUID = UUID
            .fromString("fe99385c-8265-11e5-8bcf-feff819cdc9f");

    public static final UUID CHAR_CUSTOM_CONNECTION_UUID = UUID
            .fromString("B641ABCD-62E0-4CEA-983E-498550C27305");

    public static final UUID CHAR_BIOSENSOR_CONNECTION_UUID = UUID
            .fromString("0000fff6-0000-1000-8000-00805f9b34fb");

    public static final UUID CHAR_BIOSENSOR_NOTIFY_UUID = UUID
            .fromString("0000fff7-0000-1000-8000-00805f9b34fb");

    public static final UUID OBD_SERVICE_UUID = UUID
            .fromString("0000FFF0-0000-1000-8000-00805f9b34fb");

    public static final UUID OBD_NOTIFY_CHAR_UUID = UUID
            .fromString("0000FFF1-0000-1000-8000-00805f9b34fb");

    public static final UUID OBD_WRITE_CHAR_UUID = UUID
            .fromString("0000FFF2-0000-1000-8000-00805f9b34fb");


}
