package sermk.pipi.mclient;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    private final String TAG = this.getClass().getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        //throw new UnsupportedOperationException("Not yet implemented");
        Log.v(TAG, "####boot "+ intent.toString());
        Log.w(TAG, "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
    }
}
