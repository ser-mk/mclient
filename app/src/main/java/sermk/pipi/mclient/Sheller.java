package sermk.pipi.mclient;

import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by echormonov on 22.12.17.
 */

public class Sheller {
    private final String TAG = this.getClass().getName();
    private static Shell.Interactive rootSession;

    List<String> output;

    public Sheller() {
        // start the shell in the background and keep it alive as long as the app is running
        rootSession = new Shell.Builder().
                useSU().
                setWantSTDERR(false).
                setWatchdogTimeout(15).
                setMinimalLogging(false).
                open(new Shell.OnCommandResultListener() {

                    // Callback to report whether the shell was successfully started up
                    @Override
                    public void onCommandResult(int commandCode, int exitCode, List<String> suResult) {
                        // note: this will FC if you rotate the phone while the dialog is up
                        Log.v(TAG, "commandCode: " + commandCode + "exitCode: " + exitCode);
                        final String output = TextUtils.join("\r\n", suResult);
                        Log.v(TAG, "output " +  output + "| len : " + suResult.size());

                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
                            Log.v(TAG,"Error opening root shell: exitCode " + exitCode);
                        } else {
                            // Shell is up: send our first request
                            Log.v(TAG,"// Shell is up: send our first request");
                        }
                    }
                });
    }

    public String runCommand(final String command){
        rootSession.addCommand(command);
        return "todo!";
    }
}
