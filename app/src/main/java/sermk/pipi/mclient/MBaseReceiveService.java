package sermk.pipi.mclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;

import javax.mail.Message;
import javax.mail.MessagingException;

import sermk.pipi.pilib.ErrorCollector;
import sermk.pipi.pilib.WatchConnectionMClient;


public abstract class MBaseReceiveService extends Service implements Runnable {

    final private String TAG = this.getClass().getName();

    NotificationManager mNotificationManager;

    private static final int NOTIFICATION = R.string.app_name;

    private void showNotification(final CharSequence text) {
        Log.v(TAG, "showNotification:" + text);
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Set the info for the views that show in the notification panel.
        final Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.ic_launcher)  // the status icon
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.app_name))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, LoginActivity.class), 0))  // The intent to send when the entry is clicked
                .build();

        startForeground(NOTIFICATION, notification);
        // Send the notification.
        mNotificationManager.notify(NOTIFICATION, notification);
    }

    Thread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        showNotification(TAG);
        thread = new Thread(this,"reciver thread - " + TAG);
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    protected final ErrorCollector EC = new ErrorCollector();


    @Override
    public void run() {

        while (true){
            ReceiverStruct rs = new ReceiverStruct(new AuthenticatorClient());
            Message[] messages = rs.fetch();
            EC.clear();

            succesConnection(messages.length != 0);

            FilterMessage fm = getFilterMessage();
            MessageCopy mc = new MessageCopy();
            for(Message msg: messages){
                try {
                    if(fm.checkMessage(msg) == false){
                        continue;
                    }
                    Log.i(TAG, "read mesasge number " + String.valueOf(msg.getMessageNumber())
                            + " from " + msg.getFrom()[0].toString()
                            + " with subject " + msg.getSubject()
                            + " has attached " + ReceiverStruct.hasAttachments(msg));
                    mc = copyMessage(msg);
                    //todo: replace
                    ReceiverStruct.markDelete(msg);
                    rs.release();
                    break;
                } catch (MessagingException e) {
                    e.printStackTrace();
                    EC.addError(ErrorCollector.getStackTraceString(e));
                } catch (IOException e) {
                    e.printStackTrace();
                    EC.addError(ErrorCollector.getStackTraceString(e));
                }
            }

            actionCopyMessage(mc);

            try {
                Thread.sleep(sleepMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static class MessageCopy {
        public String subj = "";
        public String content = "";
        public String filename = "";
    }

    abstract  protected MessageCopy copyMessage(Message msg);
    abstract protected boolean actionCopyMessage(final MessageCopy mc);

    protected void succesConnection(boolean succes){
        if(succes) {
            WatchConnectionMClient.sendMCConnectionResult(this,
                    WatchConnectionMClient.SUCCES);
        } else {
            WatchConnectionMClient.sendMCConnectionResult(this,
                    "problem connection");
        }
    }

    abstract  protected FilterMessage getFilterMessage();

    protected long sleepMillis(){ return 5000; }
}
