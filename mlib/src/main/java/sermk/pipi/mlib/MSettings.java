package sermk.pipi.mlib;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by echormonov on 11.12.17.
 */

public final class MSettings {

    static final public String MASTER_MAIL = "echormonov@smartlabs.tv";

    static MSettings instance = null;
    private SharedPreferences mSettings;

    final private String FIELD_MAIL = "email";
    final private String FIELD_PASSWORD = "pass";

    public MSettings(Context context) {
        mSettings = context.getSharedPreferences("msettings", Context.MODE_PRIVATE);
    }

    public static void create(Context context) {

        instance = new MSettings(context);
    }

    public static MSettings getInstance(){
        return instance;
    }

    public boolean setSelfMail(final String mail){
        mSettings.edit().putString(FIELD_MAIL,mail).apply();
        return true;
    }

    public boolean setSelfPassword(final String value){
        mSettings.edit().putString(FIELD_PASSWORD,value).apply();
        return true;
    }

    public static String getSelfMail1(){
        return "tretab873@gmail.com";
    }

    public String getSelfMail(){
        return mSettings.getString(FIELD_MAIL,"!!!!!");
    }

    public static String getSelfPassword1(){
        return "zaq1234567890";
    }

    public String getSelfPassword(){
        return mSettings.getString(FIELD_PASSWORD,"");
    }
}
