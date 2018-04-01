package sermk.pipi.mclient;


import android.app.Activity;


import android.content.Context;


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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


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

        mEmailView.setText(AuthSettings.getInstance().getSelfMail());
        mPasswordView.setText(AuthSettings.getInstance().getSelfPassword());

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


    protected void readTest(){
        CommnadReceiveService.startTest(this);
    }

    private void sendInfo(){
        final String[] fnames = {testFile("aaa"),testFile("bbb")};
        if(EventBus.getDefault().isRegistered(this) == false) {
            EventBus.getDefault().register(this);
        }
        //MTransmitterService.sendMessageAndAttachedFiles(this, "info", "content", fnames);
        MTransmitterService.sendMessageAndAttachedByteArray(this, "info", fnames[0]);
    }

    private String testFile(final String filename){
        //String filename = "test11.txt";
        String string = "Hello world!";


        try(FileOutputStream outputStream
                    = openFileOutput(filename, Context.MODE_PRIVATE) ) {
            outputStream.write(string.getBytes());
            outputStream.write(string.getBytes());
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
        AuthSettings.getInstance().setSelfMail(email);
        AuthSettings.getInstance().setSelfPassword(password);
        sendInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MTransmitterService.TransmitResult result) {
        Toast.makeText(this, "result send operation " + result, Toast.LENGTH_LONG).show();
        EventBus.getDefault().unregister(this);
    }

    private boolean isEmailValid(String emailAdd) {
        //TODO: Replace this with your own logic
        if (emailAdd == null || emailAdd.length() == 0) {
            return false;
        }

        String parttenString = "^[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\@[a-zA-Z0-9]+([\\_|\\-|\\.]?[a-zA-Z0-9])*\\.[a-zA-Z]{2,3}$";


        Pattern pattern = Pattern.compile(parttenString);
        Matcher matcher = pattern.matcher(emailAdd);

        return matcher.matches();
        //return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

}

