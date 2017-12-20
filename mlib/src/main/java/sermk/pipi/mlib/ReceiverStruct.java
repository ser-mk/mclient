package sermk.pipi.mlib;

import android.util.Log;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

/**
 * Created by echormonov on 12.12.17.
 */

public final class ReceiverStruct {

    private final String TAG = this.getClass().getName();

    Properties properties;
    AuthenticatorClient ac;
    String storeType;
    String host;

    public ReceiverStruct(AuthenticatorClient ac) {
        properties = new Properties();
        this.ac = ac;
        setDefaultProp();

    }

    private void setDefaultProp(){
        host = "imap.gmail.com";
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "995");
        properties.put("mail.imap.starttls.enable", "true");
        storeType = "imaps";
    }

    private Store store;
    private Folder emailFolder;

    public Message[] fetch() {
        Session emailSession = Session.getDefaultInstance(properties, this.ac);
        try {
            //create the POP3 store object and connect with the pop server
            store = emailSession.getStore(storeType);
            store.connect(host, this.ac._user, this.ac._pass);
            //create the folder object and open it
            emailFolder = store.getFolder("INBOX");
            emailFolder.open(Folder.READ_WRITE);
            final int unread_count  = emailFolder.getUnreadMessageCount();
            Log.v(TAG, "unread_count: " + String.valueOf(unread_count) );
            // retrieve the messages from the folder in an array and print it
            Message[] messages = emailFolder.getMessages();
            // search for all "unseen" messages
            System.out.println("messages.length:" + messages.length);
            //close the store and folder objects
        return messages;
        } catch (NoSuchProviderException e) {
            Log.e(TAG, "NoSuchProviderException.", e);
        } catch (MessagingException e) {
            Log.e(TAG, "MessagingException.", e);
        } catch (Exception e) {
            Log.e(TAG, "network not accessible", e);
        }

        return new Message[0];
    }

    public void release(){
        if(emailFolder == null)
            return;
        if(store == null)
            return;
        try {
            emailFolder.close(false);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            store.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
/*
    public Message[] filtering(Message[] messages){
        for (int i = 0, n = messages.length; i < n; i++) {
            Message msg = messages[i];
            System.out.println("---------------------------------");
            System.out.println("Email Number " + (i + 1));
            try {
                System.out.println(msg.toString());
                System.out.println("server order: " + msg.getMessageNumber());
                System.out.println("Subject: " + msg.getSubject());
                System.out.println("From: " + msg.getFrom()[0]);
                //System.out.println("Text: " + msg.getContent().toString());

                //msg.setFlag(Flags.Flag.DELETED, true);
                //System.out.println("Marked DELETE for message: " + (i + 1));

            } catch (MessagingException e) {
                Log.e(TAG, "MessagingException.", e);
        }
        return 0;
    }
    */
    public static void delete(Message msg) throws MessagingException {
        msg.setFlag(Flags.Flag.DELETED, true);
        System.out.println("Marked DELETE for message: " + msg.getMessageNumber());
    }

    public static boolean hasAttachments(Message msg) throws MessagingException, IOException {
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart)msg.getContent();
            if (mp.getCount() > 1)
                return true;
        }
        return false;
    }
}
