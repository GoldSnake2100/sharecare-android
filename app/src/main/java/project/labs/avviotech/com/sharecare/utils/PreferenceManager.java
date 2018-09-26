package project.labs.avviotech.com.sharecare.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by swayamagrawal on 30/06/17.
 */
public class PreferenceManager {


    public static SharedPreferences getDefaultSharedPreferences(Activity activity)
    {
        SharedPreferences sharedPreferences = activity.getSharedPreferences(Util.name,
                Context.MODE_PRIVATE);

        return sharedPreferences;
    }
    public static SharedPreferences getDefaultSharedPreferences(Context context)
    {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Util.name,
                Context.MODE_PRIVATE);

        return sharedPreferences;
    }
}
