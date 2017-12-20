package sermk.pipi.mclient;


import android.app.Activity;


import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;


import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

import sermk.pipi.mclient.mailwork.MBaseReceiveService;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "foo@example.com:hello", "bar@example.com:world"
    };
    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private final String TAG = this.getClass().getName();

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        mEmailView.setText(MSettings.getInstance().getSelfMail());
        mPasswordView.setText(MSettings.getInstance().getSelfPassword());

        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    checkAndSaveMail();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkAndSaveMail();
            }
        });

        Button sendinfo = (Button) findViewById(R.id.sendInfo);
        sendinfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInfo();
            }
        });

        Button readTest = (Button) findViewById(R.id.readTest);
        readTest.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                readTest();
            }
        });
    }

    private void readTest1(){
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(TestReceiveService.class.getName().equals(service.service.getClassName())) {
                Log.v(TAG, "service run");
                return;
            }
        }
        Log.v(TAG, "service stopped!!");
        startService(new Intent(this, TestReceiveService.class));
    }

    private void readTest(){
        TestReceiveService.startTest(this);
    }

    private void sendInfo1(){
        Intent intent = new Intent(this, MTransmitterService.class);
        intent.putExtra(Intent.EXTRA_TEXT, "@@@@@");
        final String[] fnames = {testFile("aaa"),testFile("bbb")};
        intent.putExtra(Intent.EXTRA_STREAM,fnames);
        intent.putExtra(Intent.EXTRA_SUBJECT, "info");
        ComponentName c = startService(intent);
        if(c == null){
            Log.v("!!", "fuckkk@ info!");
        }
        Log.v("!!","c = "  + c.toString());
    }

    private void sendInfo(){
        final String[] fnames = {testFile("aaa"),testFile("bbb")};
        EventBus.getDefault().register(this);
        MTransmitterService.sendMessage(this, "info", "content", fnames);
    }

    private String testFile(final String filename){
        //String filename = "test11.txt";
        String string = "Hello world!";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] fnames = this.fileList();
        for (String str : fnames) {
            Log.v(TAG,"name file: " + str);
        }
        File f = new File(getFilesDir(), filename);
        if(f.exists()){
            Log.v(TAG, "f.exists() " + f.getName() + " " + f.getAbsolutePath() );
        } else {
            Log.v(TAG, "f not!! exists()");
        }

        return f.getAbsolutePath();
    }

    private String testFile2(){
        String filename = "test11.txt";
        String string = "Hello world!";
        File file = new File(filename);
        try {
            FileWriter fw = new FileWriter(file);

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] fnames = this.fileList();
        for (String str : fnames) {
            Log.v(TAG,"name file: " + str);
        }
        return getFilesDir() + filename;
    }


    void test(){
        //Intent intent = new Intent("sermk.pipi.mclient.MTransmitterService");
        Intent intent = new Intent();
        intent.setClassName("sermk.pipi.mclient", "sermk.pipi.mclient.MTransmitterService");
        intent.putExtra("2131", "213");
        Log.v("!!!!!! ", "sending1");
        ComponentName c = startService(intent);
        if(c == null){
            Log.v("!!", "fuckkk@");
        }
        Log.v("!!!!!! ", "sending " + c.toString());
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void checkAndSaveMail() {

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        final String email = mEmailView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            return;
        }
        MSettings.getInstance().setSelfMail(email);
        MSettings.getInstance().setSelfPassword(password);
        sendInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MTransmitterService.TransmitResult result) {
        Toast.makeText(this, "result send operation " + result, Toast.LENGTH_LONG).show();
        EventBus.getDefault().unregister(this);
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

