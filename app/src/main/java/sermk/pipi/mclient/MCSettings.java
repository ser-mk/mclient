package sermk.pipi.mclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Field;

import sermk.pipi.pilib.PiUtils;

/**
 * Created by ser on 01.04.18.
 */

class MCSettings {
    static public class Settings {
        public int timeout_ms_reciever = 10000;
        public long timeout_ms_location_reciever = 0;
        public long distance_meter_location_reciever = 1;
    }

    static final String TAG = "MCSettings";
    static private Settings settings;

    static public Settings getMCSettingInstance(Context context){
        if(settings != null){
            return settings;
        }
        final String json = PiUtils.getJsonFromShared(context);

        final Settings temp = new Gson().fromJson(json, Settings.class);
        if(PiUtils.checkHasNullPublicField(temp, Settings.class)){
            Log.v(TAG,"settings broken!");
            MCSettings.settings = new Settings();
            return MCSettings.settings;
        }

        MCSettings.settings = temp;
        return MCSettings.settings;
    }

    static public boolean saveMCSettingInstance(Context context, String json){

        if(PiUtils.checkHasNullPublicField(settings, Settings.class)){
            Log.v(TAG,"object settings has null object!");
            return false;
        }

        PiUtils.saveJson(context, json);

        return true;
    }
}
