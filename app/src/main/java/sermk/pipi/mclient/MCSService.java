package sermk.pipi.mclient;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import sermk.pipi.mclient.mailwork.AuthenticatorClient;
import sermk.pipi.mclient.mailwork.Transmitter;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MCSService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "sermk.pipi.mailcontroller.action.FOO";
    private static final String ACTION_BAZ = "sermk.pipi.mailcontroller.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "sermk.pipi.mailcontroller.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "sermk.pipi.mailcontroller.extra.PARAM2";

    public MCSService() {
        super("MCSService");
    }

    NotificationManager mNotificationManager;
    final String TAG = "MCSService";
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
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MCSService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MCSService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent != null) {
            Log.v(TAG, "intent : " + intent.toString());
            final String body = intent.getStringExtra(Intent.EXTRA_TEXT);
            final String[] attachedFiles = intent.getStringArrayExtra(Intent.EXTRA_STREAM);
            final String subject = intent.getStringExtra(Intent.EXTRA_SUBJECT);
            sendMessage(subject, body, attachedFiles);
        } else {
            Log.v(TAG, "intent null!");
        }
    }

    private boolean sendMessage(final String subject, final String body, final String[] attachedFiles){
        final AuthenticatorClient ac = new AuthenticatorClient();
        Transmitter tr = new Transmitter(ac);
        tr.setBody(body);
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
            return false;
        }
        return true;
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
