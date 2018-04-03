package sermk.pipi.mclient;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;

import java.lang.reflect.Field;

/**
 * Created by ser on 01.04.18.
 */

class MCSettings {
    static public class Store {
        public int timeout_ms_reciever = 1000;
        public long timeout_ms_location_reciever = 1000;
        public long distance_meter_location_reciever = 1;
    }

    static final String TAG = "MCSettings";
    static private Store store;
    static private final String SHARED_PREF_NAME = "MCSettings";
    static private final String FIELD_JSON_STORE = "all_settings";


    static public Store getMCSettingInstance(Context context){
        if(store != null){
            return store;
        }
        final SharedPreferences settings =
                context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);

        final String json = settings.getString(FIELD_JSON_STORE, "");

        if(json.isEmpty()){
            Log.v(TAG,"settings not found!");
            store = new Store();
            return store;
        }
        final Store temp = new Gson().fromJson(json, Store.class);
        if(temp == null){
            Log.v(TAG,"settings broken!");
            store = new Store();
            return store;
        }

        store = temp;
        return store;
    }

    static String getJsonStore(Context context){
        final SharedPreferences settings =
                context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return settings.getString(FIELD_JSON_STORE, "");
    }

    static public boolean saveMCSettingInstance(Context context, String json){

        final Store temp = new Gson().fromJson(json, Store.class);
        if(temp == null){
            Log.v(TAG,"json broken!");
            return false;
        }
        if(checkNull(temp)){
            Log.v(TAG,"json has null object!");
            return false;
        }

        final SharedPreferences mSettings =
                context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        mSettings.edit().putString(FIELD_JSON_STORE, json).apply();

        return true;
    }

    static public boolean checkNull(Store tmp){
        try {
            for (Field f : Store.class.getDeclaredFields())
                    if (f.get(tmp) == null)
                        return true;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }
}
