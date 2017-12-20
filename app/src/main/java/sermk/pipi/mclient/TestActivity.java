package sermk.pipi.mclient;

import sermk.pipi.mlib.LoginActivity;

/**
 * Created by echormonov on 20.12.17.
 */

public final class TestActivity extends LoginActivity {

    @Override
    protected void readTest(){
        TestReceiveService.startTest(this);
    }
}
