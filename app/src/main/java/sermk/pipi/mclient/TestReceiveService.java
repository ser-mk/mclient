package sermk.pipi.mclient;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import sermk.pipi.mclient.mailwork.FilterMessage;
import sermk.pipi.mclient.mailwork.MBaseReceiveService;

/**
 * Created by echormonov on 20.12.17.
 */

public class TestReceiveService extends MBaseReceiveService {


    public static void startTest(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(TestReceiveService.class.getName().equals(service.service.getClassName())) {
                Log.v("startTest", "service run");
                return;
            }
        }
        Log.v("startTest", "service stopped!!");
        context.startService(new Intent(context, TestReceiveService.class));
    }

    @Override
    protected FilterMessage getFilterMessage(){
        final String[] from = {MSettings.MASTER_MAIL};
        final String[] subject = {"test"};
        return new FilterMessage(from, subject);
    }

    @Override
    protected long sleepMillis() {
        return super.sleepMillis();
    }
}
