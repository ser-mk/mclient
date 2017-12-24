package sermk.pipi.mclient;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import sermk.pipi.mlib.LoginActivity;

/**
 * Created by echormonov on 20.12.17.
 */

public final class TestActivity extends LoginActivity {

    private final String TAG = this.getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG,"???????");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intt = new Intent("PING");
        sendBroadcast(intt);
        Log.v(TAG,"!!!!!!!!");
    }

    @Override
    protected void readTest(){
        CommnadReceiveService.startTest(this);
    }
}
