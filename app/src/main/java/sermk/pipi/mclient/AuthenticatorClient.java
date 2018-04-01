package sermk.pipi.mclient;

import javax.mail.PasswordAuthentication;

/**
 * Created by echormonov on 11.12.17.
 */

public final class AuthenticatorClient extends javax.mail.Authenticator {

    String  _user;
    String  _pass;

    public AuthenticatorClient(String _user, String _pass) {
        this._user = _user;
        this._pass = _pass;
    }

    public AuthenticatorClient() {
        this._user = AuthSettings.getInstance().getSelfMail();
        this._pass = AuthSettings.getInstance().getSelfPassword();
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    boolean isValid(){ return true; }
}
