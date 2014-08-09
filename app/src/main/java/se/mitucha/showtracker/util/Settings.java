package se.mitucha.showtracker.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Set;

import se.mitucha.showtracker.view.TimePreference;

/**
 * Created by mr11011 on 2014-08-07.
 */
public class Settings {

    private static Settings settings = null;
    private SharedPreferences sharedPreferences;

    public static final String NOTIFICATION_UPDATE = "notificationUpdate";
    public static final String NOTIFICATION_INTERVAL = "notificationInterval";
    public static final String CONNECTION_METHOD = "connection_method";
    public static void makeSettings(Context context){
        settings = (settings == null)?new Settings(context):settings;
    }

    public static Settings getSettings(){
        return settings;
    }

    private Settings(Context context){
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public boolean getNotificationUpdate(){
        return sharedPreferences.getBoolean(NOTIFICATION_UPDATE,true);
    }

    public int getUpdateInterval(){
        String time = sharedPreferences.getString(NOTIFICATION_INTERVAL, "12:00");
        return TimePreference.getHour(time) *(1000*60*60) + TimePreference.getMinute(time)*(1000*60);
    }

    public int getConnectionMethod(){
        Set<String> set = sharedPreferences.getStringSet(CONNECTION_METHOD, null);
        int result = 0;
        if(set!=null)
            for (String item : set)
                result += Integer.parseInt(item);
        else
            result = 12;
        return result;
    }



}
