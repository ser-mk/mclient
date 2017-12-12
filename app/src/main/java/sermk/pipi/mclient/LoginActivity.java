package sermk.pipi.mclient;


import android.app.Activity;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.os.AsyncTask;


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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

    final private String TAG = this.getClass().getName();

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
    private UserLoginTask mAuthTask = null;

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
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                //attemptLogin();
                test();
            }
        });

        Button sendinfo = (Button) findViewById(R.id.sendInfo);
        sendinfo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                sendInfo();
            }
        });

    }

    private void sendInfo(){
        Intent intent = new Intent(this, MCSService.class);
        intent.putExtra(Intent.EXTRA_TEXT, "@@@@@");
        final String[] fnames = {testFile("aaa"),testFile("bbb")};
        intent.putExtra(Intent.ACTION_ATTACH_DATA,fnames);
        intent.putExtra(Intent.EXTRA_SUBJECT, "info");
        ComponentName c = startService(intent);
        if(c == null){
            Log.v("!!", "fuckkk@ info!");
        }
        Log.v("!!","c = "  + c.toString());
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
        //Intent intent = new Intent("sermk.pipi.mclient.MCSService");
        Intent intent = new Intent();
        intent.setClassName("sermk.pipi.mclient", "sermk.pipi.mclient.MCSService");
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
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

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
        } else {
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mEmail)) {
                    // Account exists, return true if the password matches.
                    return pieces[1].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

