package sermk.pipi.mclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import sermk.pipi.pilib.CommandCollection;
import sermk.pipi.pilib.ErrorCollector;

public class SettingsReciever extends BroadcastReceiver {

    private String TAG = this.getClass().getName();
    private final ErrorCollector EC = new ErrorCollector();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        EC.clear();
        Log.v(TAG, "inent: " + intent.toString());

        String action = "";
        try{
            action = intent.getAction().trim();
        } catch (Exception e){
            action = "wrong action!";
            Log.w(TAG, "action is not exist!");
        }
        Log.v(TAG, action);

        String content = "";
        try{
            content = intent.getStringExtra(Intent.EXTRA_TEXT);
            content.isEmpty();
        } catch (Exception e){
            content = "wrong content!";
            Log.w(TAG, "content is not exist!");
        }
        Log.v(TAG, content);

        byte[] array = new byte[0];
        try{
            array = intent.getByteArrayExtra(Intent.EXTRA_INITIAL_INTENTS);
            array.hashCode();
        } catch (Exception e){
            array = "wrong byte array !".getBytes();
            Log.w(TAG, "attached data absent!");
        }

        String error = ErrorCollector.NO_ERROR;

        try {
            error = doAction(context, content, action);
        } catch (Exception e){
            e.printStackTrace();
            error = e.toString();
        }

        if(ErrorCollector.NO_ERROR.equals(error)) return;

        EC.addError(error);

        Log.v(TAG, EC.error);

        MTransmitterService.sendMessageText(context, EC.subjError(TAG,action), EC.error);
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
            final String json = MCSettings.getJsonStore(context);
            MTransmitterService.sendMessageText(context,action,json);
        } else {
            return "undefined Action for SettingsReciever";
        }

        return ErrorCollector.NO_ERROR;
    }
}
