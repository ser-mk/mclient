package sermk.pipi.mclient;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

import javax.mail.Message;

import eu.chainfire.libsuperuser.Shell;
import sermk.pipi.mlib.FilterMessage;
import sermk.pipi.mlib.MBaseReceiveService;
import sermk.pipi.mlib.MSettings;
import sermk.pipi.mlib.MTransmitterService;
import sermk.pipi.mlib.ReceiverStruct;

/**
 * Created by echormonov on 20.12.17.
 */

public class TestReceiveService extends MBaseReceiveService {

    private final String TAG = this.getClass().getName();

    private final String SUBJECT_COMMAND = "test";
    private final String RESULT_PREFIX = "result: ";


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
        final String[] subject = {SUBJECT_COMMAND};
        return new FilterMessage(from, subject);
    }

    @Override
    protected MessageCopy copyMessage(Message msg){
        MessageCopy mc = new MessageCopy();
        try {
            mc.subj = msg.getSubject();
            final String from = msg.getFrom()[0].toString();
            mc.content = ReceiverStruct.getText(msg);
            Log.v(TAG, "From " + from + " | Content " + mc.content);
            if(ReceiverStruct.hasAttachments(msg)){
                mc.filename = ReceiverStruct.saveAttachmentFile(this,msg);
                Log.v(TAG, "Receiver filename " + mc.filename);
            }
            Log.v(TAG, "text : " + ReceiverStruct.getText(msg));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mc;
    }

    @Override
    protected boolean actionCopyMessage(final MessageCopy mc) {
        if(mc.subj.isEmpty()){
            return false;
        }
        if(mc.subj.equals(SUBJECT_COMMAND)){
            doCommand(mc);
        }
        return true;
    }

    static private String getFirstLine(final String str){
        return str.split("\r\n|\r|\n")[0]; //bad
        //return str.substring(0, str.indexOf("\r\n"));
    }

    private boolean doCommand(final MessageCopy mc){
        String command = getFirstLine(mc.content);
        command +=" " + mc.filename;
        final String result = runShell(command);
        return MTransmitterService.sendMessageText(this,RESULT_PREFIX + mc.subj, result);
    }

    static private String runShell(final String command){
        if (!Shell.SU.available()){
            return "su unvailable =(";
        }
        final List<String> suResult = Shell.SU.run( command );
        final String output = TextUtils.join("\r\n", suResult);
        return command + " # " + output;

    }

    @Override
    protected long sleepMillis() {
        return super.sleepMillis();
    }
}
