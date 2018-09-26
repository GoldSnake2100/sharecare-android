package project.labs.avviotech.com.sharecare.utils;

/**
 * Created by NJX on 3/11/2018.
 */

public class API {

    // dev server
//    public static final String SERVER_URL = "http://192.168.2.107:8080/ShareCareServer/";
    public static final String SERVER_URL = "http://74.50.54.231:8080/ShareCareServer/";

    public static final String GET_ALL_USERS = SERVER_URL + "user";
    public static final String LOGIN = SERVER_URL + "user/login";
    public static final String SIGNUP = SERVER_URL + "user/signup";
    public static final String UPDATE_LOCATION = SERVER_URL + "user/updateLocation";
    public static final String UPDATE_PROFILE = SERVER_URL + "user/updateUser";
    public static final String UPDATE_PROFILE_PHOTO = SERVER_URL + "user/updateUserPhoto";

    public static final String UPLOAD_CHILD_PHOTO = SERVER_URL + "user/photo/{email}";
    public static final String PHOTO_BASEPATH = "http://74.50.54.231:8080/uploadphoto/";

    public static final String SEARCH_USER = SERVER_URL + "user/searchUser";
    public static final String GET_CAREGIVERS = SERVER_URL + "user/caregiver";
    public static final String REFER_USER = SERVER_URL + "user/referUser";
    public static final String DELETE_CAREGIVER = SERVER_URL + "user/caregiver";

    public static final String APP_DOWNLOAD_LINK = SERVER_URL + "/user/downloadLink";
    public static final String FORGOT_PASSWORD = SERVER_URL + "/sendEmail/forgotPassword";
}
