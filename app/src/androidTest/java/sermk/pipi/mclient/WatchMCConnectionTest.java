package sermk.pipi.mclient;

/**
 * Created by ser on 04.04.18.
 */

import android.app.Activity;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;

import sermk.pipi.pilib.WatchConnectionMClient;

import static junit.framework.Assert.*;

public class WatchMCConnectionTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void sendMCConnectionResult_test() throws Exception {
        long TIMEOUT  = 10*1000;
        long FINE = 0;
        WatchConnectionMClient wc = new WatchConnectionMClient(TIMEOUT, FINE);
        Activity act = activityTestRule.getActivity();
        wc.init(act);
        Thread.sleep(200);
        WatchConnectionMClient.sendMCConnectionResult(act, "FUCK");
        assertTrue(wc.checkTimeout());

        WatchConnectionMClient.sendMCConnectionResult(act, WatchConnectionMClient.SUCCES);
        int i = 0;
        while (wc.checkTimeout()){
            Thread.sleep(200);
            i++;
        }

        System.out.println("Success connetion! " + i);

        assertFalse(wc.checkTimeout());

        i = 0;
        while (!wc.checkTimeout()){
            Thread.sleep(1000);
            i++;
        }

        System.out.println("timeut ! " + i);

        assertTrue(i > 8);

        WatchConnectionMClient.sendMCConnectionResult(act, WatchConnectionMClient.SUCCES);
        while (wc.checkTimeout()){
            Thread.sleep(2);
        }

        i = 0;
        while (!wc.checkTimeout()){
            Thread.sleep(1000);
            i++;
            WatchConnectionMClient.sendMCConnectionResult(act, "FUCK");
        }

        System.out.println("timeut wtih 0 fine! " + i);

        assertTrue(i > 8);

        FINE = 1000;
        wc = new WatchConnectionMClient(TIMEOUT, FINE);

        WatchConnectionMClient.sendMCConnectionResult(act, WatchConnectionMClient.SUCCES);
        while (wc.checkTimeout()){
            Thread.sleep(2);
        }

        i = 0;
        while (!wc.checkTimeout()){
            Thread.sleep(1000);
            i++;
            WatchConnectionMClient.sendMCConnectionResult(act, "FUCK");
        }

        System.out.println("timeut wtih 1000 fine! " + i);

        assertTrue(i < 6);
    }
}
