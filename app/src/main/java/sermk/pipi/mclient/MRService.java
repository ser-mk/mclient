package sermk.pipi.mclient;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import javax.mail.Message;
import javax.mail.MessagingException;

import sermk.pipi.mclient.mailwork.AuthenticatorClient;
import sermk.pipi.mclient.mailwork.FilterMessage;
import sermk.pipi.mclient.mailwork.ReceiverStruct;

public class MRService extends Service implements Runnable {

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
        thread = new Thread(this,"reciver " + TAG);
        thread.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void run() {

        while (true){
            ReceiverStruct rs = new ReceiverStruct(new AuthenticatorClient());
            Message[] messages = rs.fetch();

            FilterMessage fm = getFilterMessage();
            for(Message msg: messages){
                try {
                    if(fm.checkMessage(msg) == false){
                        continue;
                    }
                    Log.v(TAG, "read mesasge number " + String.valueOf(msg.getMessageNumber())
                            + " from " + msg.getFrom()
                            + " with subject " + msg.getSubject());
                    ReceiverStruct.delete(msg);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }

            rs.release();

            try {
                Thread.sleep(sleepMillis());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    protected void succesConnection(boolean succes){

    }

    protected FilterMessage getFilterMessage(){
        final String[] from = {Settings.MASTER_MAIL};
        final String[] subject = {"test"};
        return new FilterMessage(from, subject);
    }

    protected long sleepMillis(){ return 5000; }
}
