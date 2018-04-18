package sermk.pipi.mclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import sermk.pipi.pilib.CommandCollection;
import sermk.pipi.pilib.ErrorCollector;
import sermk.pipi.pilib.PiUtils;
import sermk.pipi.pilib.UniversalReciver;

public class SettingsReciever extends BroadcastReceiver {

    private String TAG = this.getClass().getName();
    private final ErrorCollector EC = new ErrorCollector();

    @Override
    public void onReceive(Context context, Intent intent) {
        EC.clear();

        final UniversalReciver.ReciverVarible rv
                = UniversalReciver.parseIntent(intent, TAG);

        String error = ErrorCollector.NO_ERROR;

        try {
            error = doAction(context, rv.content, rv.action);
        } catch (Exception e){
            e.printStackTrace();
            error = e.toString();
        }

        if(ErrorCollector.NO_ERROR.equals(error)) return;

        EC.addError(error);

        Log.v(TAG, EC.error);

        MTransmitterService.sendMessageText(context,
                EC.subjError(TAG,rv.action), EC.error);
    }

    private String doAction(Context context, final String content, @NonNull final String action){
        if(CommandCollection.
                ACTION_RECIVER_MCLIENT_SET_AND_SAVE_SETTINGS.
                equals(action)){
            final boolean success = MCSettings.saveMCSettingInstance(context,content);
            if(!success){
                return "cant save settings!";
            }
        } else if(CommandCollection.
                ACTION_RECIVER_FOR_ALL_QUERY_SETTINGS.
                equals(action)){
            final String settings = getSettings(context);
            Log.v(TAG,"settings for send " + settings);
            MTransmitterService.sendMessageText(context,action,settings);
        } else {
            return "undefined Action for SettingsReciever";
        }

        return ErrorCollector.NO_ERROR;
    }

    private String getSettings(Context context){
        final String json = PiUtils.getJsonFromShared(context);
        final String id = MUtils.md5(AuthSettings.getInstance().getSelfPassword());
        return json + "\r\nid: " + id;
    }
}
