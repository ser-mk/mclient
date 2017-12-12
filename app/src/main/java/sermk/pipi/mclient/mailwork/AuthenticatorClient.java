package sermk.pipi.mclient.mailwork;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

import sermk.pipi.mclient.Settings;

/**
 * Created by echormonov on 11.12.17.
 */

public final class AuthenticatorClient extends javax.mail.Authenticator {

    String  _user;
    private String  _pass;

    public AuthenticatorClient(String _user, String _pass) {
        this._user = _user;
        this._pass = _pass;
    }

    public AuthenticatorClient() {
        this._user = Settings.getSelfMail();
        this._pass = Settings.getSelfPassword();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    boolean isValid(){ return true; }
}
