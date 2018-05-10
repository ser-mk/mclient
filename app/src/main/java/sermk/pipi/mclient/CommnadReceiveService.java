package sermk.pipi.mclient;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.util.List;

import javax.mail.Message;

import eu.chainfire.libsuperuser.Shell;
import sermk.pipi.pilib.ErrorCollector;
import sermk.pipi.pilib.MClient;

/**
 * Created by echormonov on 20.12.17.
 */

public class CommnadReceiveService extends MBaseReceiveService {

    private final String TAG = this.getClass().getName();

    private final String SUBJECT_SHELL = "shell";
    private final String SUBJECT_BROADCAST = "broadcast";
    private final String BROADCAST_SEPARATOR = "-";
    private final String RESULT_PREFIX = "result: ";
    private final String FAILED_PREFIX = " failed ";

    public static void startTest(Context context){
        final String TAG_CONTEXT = context.getClass().getName();
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(CommnadReceiveService.class.getName().equals(service.service.getClassName())) {
                Log.i(TAG_CONTEXT, "service run");
                return;
            }
        }
        Log.i(TAG_CONTEXT, "service stopped!!");
        context.startService(new Intent(context, CommnadReceiveService.class));
    }

    private int timeout_ms_sleep = 1000;

    @Override
    public void onCreate() {
        super.onCreate();

        Log.i(TAG, "creating service reciver with timeout_ms_sleep " + timeout_ms_sleep);
        timeout_ms_sleep = MCSettings.getMCSettingInstance(this).timeout_ms_reciever;
    }

    @Override
    protected FilterMessage getFilterMessage(){
        final String[] from = AuthSettings.MASTER_MAIL;
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
            EC.addError(ErrorCollector.getStackTraceString(e));
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
        if(ret == false){
            MClient.sendMessage(this,
                    ErrorCollector.subjError(TAG,mc.subj)
                    , EC.error);
        }
        return ret;
    }

    static private String getFirstLine(final String str){
        return str.split("\r\n|\r|\n")[0]; //bad
    }

    private boolean doBroadCastMessage(final MessageCopy mc){
        Log.v(TAG, mc.subj);

        String[] slpitSubj = mc.subj.split(BROADCAST_SEPARATOR);
        String broadcastReceiverAction = "";
        String broadcastReceiverPackage = "";
        String broadcastReceiverName = "";

        try{
            broadcastReceiverAction = slpitSubj[1].trim();
        } catch (Exception e){
            e.printStackTrace();
            EC.addError(ErrorCollector.getStackTraceString(e));
            return false;
        }
        try {broadcastReceiverPackage = slpitSubj[2].trim();}
        catch (Exception e) {broadcastReceiverPackage = "";
            EC.addError(ErrorCollector.getStackTraceString(e));}

        try { broadcastReceiverName = slpitSubj[3].trim(); }
        catch (Exception e) { broadcastReceiverName = "";
        EC.addError(ErrorCollector.getStackTraceString(e));}

        return sendBroadCastMessage(broadcastReceiverAction,
                broadcastReceiverPackage,
                broadcastReceiverName,
                mc.content,mc.filename);
    }

    private boolean sendBroadCastMessage(final String action,
                                         final String packageName, final String receiverName,
                                         final String content,
                                         final String filename) {
        Intent intent = new Intent(action);/*
        intent.putExtra(NameFieldCollection.FIELD_RECIVER_DATA_TEXT, content);
        if(!packageName.isEmpty() && !receiverName.isEmpty()){
            intent.setComponent(new ComponentName(packageName, receiverName));
        }
        if(packageName.isEmpty()){  //Todo it's wrong!
            intent.putExtra(NameFieldCollection.FIELD_RECIVER_ATTACHED_BYTES, MUtils.getByteOfFile(filename));
        } else {
            intent.putExtra(Intent.EXTRA_STREAM,
                    getUriFile(packageName, filename).toString());
        }
*/
        sendBroadcast(intent);
        return true;
    }

    private Uri getUriFile(final String packagename, final String filename){
        Uri uri = Uri.EMPTY;
        try {
            File file = new File(filename);
            uri = android.support.v4.content.FileProvider.
                    getUriForFile(this, BuildConfig.APPLICATION_ID, file);
            grantUriPermission(packagename, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } catch (Exception e){
            uri = Uri.EMPTY;
            EC.addError("Uri.EMPTY");
        }
        Log.v(TAG, "broadcast attached URI = " + uri);
        return uri;
    }


    private boolean doShellMessage(final MessageCopy mc){
        String command = "empty comand!";
        try{ command = getFirstLine(mc.content);}
        catch (Exception e){
            EC.addError(ErrorCollector.getStackTraceString(e));
            return false; }
        command +=" " + mc.filename;
        final String result = runShell(command);
        Log.v(TAG, "result shell command: " + result);
        return MTransmitterService.sendMessageText(this,RESULT_PREFIX + mc.subj, result);
    }

    static private String runShell(final String command){
        if (!Shell.SU.available()){return "su unvailable =(";}
        final List<String> suResult = Shell.SU.run( command );
        final String output = TextUtils.join("\r\n", suResult);
        return command + " # " + output;
    }

    @Override
    protected long sleepMillis() {
        //return super.sleepMillis();
        return timeout_ms_sleep;
    }
}
