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
import sermk.pipi.mlib.MUtils;
import sermk.pipi.mlib.ReceiverStruct;

/**
 * Created by echormonov on 20.12.17.
 */

public class CommnadReceiveService extends MBaseReceiveService {

    private final String TAG = this.getClass().getName();

    private final String SUBJECT_SHELL = "shell";
    private final String SUBJECT_BROADCAST = "broadcast";
    private final String BROADCAST_SEPARATOR = "-";
    private final String RESULT_PREFIX = "result: ";


    public static void startTest(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(CommnadReceiveService.class.getName().equals(service.service.getClassName())) {
                Log.v("startTest", "service run");
                return;
            }
        }
        Log.v("startTest", "service stopped!!");
        context.startService(new Intent(context, CommnadReceiveService.class));
    }

    @Override
    protected FilterMessage getFilterMessage(){
        final String[] from = {MSettings.MASTER_MAIL};
        final String[] subject = {SUBJECT_SHELL, SUBJECT_BROADCAST};
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
        boolean ret = false;
        if(FilterMessage.equalsSubject(mc.subj,SUBJECT_SHELL)){
            ret = doShellMessage(mc);
        }
        if(FilterMessage.equalsSubject(mc.subj,SUBJECT_BROADCAST)){
            ret = doBroadCastMessage(mc);
        }
        //todo: send result false operation!
        return ret;
    }

    static private String getFirstLine(final String str){
        return str.split("\r\n|\r|\n")[0]; //bad
    }

    private boolean doBroadCastMessage(final MessageCopy mc){
        Log.v(TAG, mc.subj);

        String[] slpitSubj = mc.subj.split(BROADCAST_SEPARATOR);
        String broadcastReceiverName = "";
        String broadcastReceiverAction = "";
        try{
            broadcastReceiverName = slpitSubj[1];
            broadcastReceiverAction = slpitSubj[2];
            broadcastReceiverName.trim();
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return sendBroadCastMessage(
                broadcastReceiverName,broadcastReceiverAction,
                mc.content,mc.filename);
    }

    private boolean sendBroadCastMessage(final String name,
                                         final String action,
                                         final String content,
                                         final String filename) {
        Intent intent = new Intent(name);
        intent.putExtra(Intent.ACTION_MAIN, action);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                MUtils.getByteOfFile(filename));
        sendBroadcast(intent);
        return true;
    }


    private boolean doShellMessage(final MessageCopy mc){
        String command = "empty comand!";
        try{ command = getFirstLine(mc.content);}
        catch (Exception e){ return false; }
        command +=" " + mc.filename;
        final String result = runShell(command);
        Log.v(TAG, "result shell command: " + result);
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
