package sermk.pipi.mclient;

import android.support.test.rule.ActivityTestRule;

import com.google.gson.Gson;

import org.junit.Rule;
import org.junit.Test;

import static junit.framework.Assert.*;

/**
 * Created by ser on 03.04.18.
 */
public class MCSettingsTest {

    @Rule
    public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void saveMCSettingInstance() throws Exception {
        MCSettings.Settings tmp = MCSettings.getMCSettingInstance(activityTestRule.getActivity());

        String js = new Gson().toJson(tmp);

        System.out.println(js);

        boolean res = MCSettings.saveMCSettingInstance(activityTestRule.getActivity(), js);

        assertTrue(res);




    }
}