package sermk.pipi.mlib;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MTransmitterService extends IntentService {

    public enum TransmitResult{ SUCCES, FAILED };

    //final private String TAG = this.getClass().getName();

    public MTransmitterService() {
        super("MTransmitterService");
    }

    NotificationManager mNotificationManager;
    final String TAG = "MTransmitterService";
    private static final int NOTIFICATION = R.string.app_name;

    private void showNotification(final CharSequence text) {
        Log.v(TAG, "showNotification:" + text);
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

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static boolean sendMessage(Context context, @NonNull final String subject,
                                      @NonNull final String content,
                                      @NonNull final String[] attached_files) {
        Intent intent = new Intent(context, MTransmitterService.class);
        intent.putExtra(Intent.EXTRA_TEXT, content);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        if(attached_files.length != 0) {
            intent.putExtra(Intent.EXTRA_STREAM, attached_files);
        }
        ComponentName c = context.startService(intent);
        if(c == null){
            Log.w("sendMessage", "can not send(");
            return false;
        }
        Log.v("sendMessage","send succes!");
        return true;
    }


    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            Log.v(TAG, "intent : " + intent.toString());
            final String body = intent.getStringExtra(Intent.EXTRA_TEXT);
            final String[] attachedFiles = intent.getStringArrayExtra(Intent.EXTRA_STREAM);
            final String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            final TransmitResult ret = sendMessage(subject, body, attachedFiles);
            EventBus.getDefault().post(ret);
        } else {
            Log.v(TAG, "intent null!");
        }
    }

    private TransmitResult sendMessage(final String subject, final String body, final String[] attachedFiles){
        final AuthenticatorClient ac = new AuthenticatorClient();
        Transmitter tr = new Transmitter(ac);
        tr.setBody(body + getVersionInfo());
        tr.set_subject(subject);
        for (String names : attachedFiles) {
            try {
                tr.addAttachment(names);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            tr.send();
        } catch (Exception e) {
            e.printStackTrace();
            return TransmitResult.FAILED;
        }
        return TransmitResult.SUCCES;
    }

    //get the current version number and name
    private String getVersionInfo() {
        String versionName = "error";
        int versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "\r\n" + "app: " + getApplicationContext().getPackageName() + " version: " + versionName + " code: " + String.valueOf(versionCode);
    }

}
