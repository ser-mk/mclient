package sermk.pipi.mclient;

import android.content.Context;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;

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
        //setDefaultPropIMAP();
        setDefaultPropPOP3();
    }

    private void setDefaultPropIMAP(){
        host = "imap.yandex.ru";
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        storeType = "imaps";
    }

    private void setDefaultPropPOP3(){
        host = "pop.yandex.ru";
        properties.put("mail.pop3.host", host);
        properties.put("mail.pop3.port", "995");
        properties.put("mail.pop3.starttls.enable", "true");
        storeType = "pop3s";
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
            Log.i(TAG, "close inbox folder");
            emailFolder.close(true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        try {
            Log.i(TAG, "close pop3s store");
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
    public static void markDelete(Message msg) throws MessagingException {
        msg.setFlag(Flags.Flag.DELETED, true);
        System.out.println("Marked DELETE and save changes for message: " + msg.getMessageNumber());
    }

    public static boolean hasAttachments(Message msg) throws MessagingException, IOException {
        if (msg.isMimeType("multipart/mixed")) {
            Multipart mp = (Multipart)msg.getContent();
            if (mp.getCount() > 1)
                return true;
        }
        return false;
    }

    /**
     * Determine whether to include attachments
     * @param part
     * @return
     * @throws MessagingException
     * @throws IOException
     */
    /*
    public static  boolean isContainAttachments(Part part){
        boolean flag = false;
        try
        {
            String contentType = part.getContentType();
            if(part.isMimeType("multipart/*")){
                // Multipart multipart = (Multipart) part.getContent();
                DataSource source = new ByteArrayDataSource(part.getInputStream(), "multipart/*");
                Multipart multipart = new MimeMultipart(source);
                int count = multipart.getCount();
                for(int i = 0;i < count;i++){
                    BodyPart bodypart = multipart.getBodyPart(i);
                    String disPosition = bodypart.getDisposition();
                    if((disPosition != null)&&(disPosition.equals(Part.ATTACHMENT)||disPosition.equals(Part.INLINE))){
                        flag = true;
                    }else if(bodypart.isMimeType("multipart/*")){
                        flag = isContainAttachments(bodypart);
                    }else{
                        String conType = bodypart.getContentType();
                        if(conType.toLowerCase().indexOf("appliaction") != -1){
                            flag = true;
                        }
                        if(conType.toLowerCase().indexOf("name") != -1){
                            flag = true;
                        }
                    }
                    if (flag) break;
                }
            }else if(part.isMimeType("message/rfc822")){
                flag = isContainAttachments((Part) part.getContent());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return flag;
    }

*/

    public static  String getText(Part p) throws MessagingException, IOException {
        if (p.isMimeType("text/*")) {
            String s = (String)p.getContent();
            final boolean textIsHtml = p.isMimeType("text/html");
            if(textIsHtml){
                return "!html!" + s;
            }
            return s;
        }

        if (p.isMimeType("multipart/alternative")) {
            // prefer html text over plain text
            Multipart mp = (Multipart)p.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text = getText(bp);
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = getText(bp);
                    if (s != null)
                        return s;
                } else {
                    return getText(bp);
                }
            }
            return text;
        } else if (p.isMimeType("multipart/*")) {
            Multipart mp = (Multipart)p.getContent();
            for (int i = 0; i < mp.getCount(); i++) {
                String s = getText(mp.getBodyPart(i));
                if (s != null)
                    return s;
            }
        }

        return "!empty!";
    }

    public static String saveAttachmentFile(Context context, Part part) throws MessagingException, IOException{
        String fileName = "" ;
        if(part.isMimeType("multipart/*")){
            DataSource source = new ByteArrayDataSource(part.getInputStream(), "multipart/*");
            Multipart mp = new MimeMultipart(source);
            for(int i = 0;i < mp.getCount(); i++){
                BodyPart  bodyPart = mp.getBodyPart(i);
                String disposition = bodyPart.getDisposition();
                InputStream stream = (InputStream) bodyPart.getInputStream();

                if((disposition != null)&&(disposition.equals(Part.ATTACHMENT)||disposition.equals(Part.INLINE))){
                    fileName = bodyPart.getFileName();
                    if(fileName != null){
                        Log.v("saveAttachmentFile" ,
                                "first " + fileName);
                        if (fileName.toLowerCase().indexOf("gb2312") != -1){
                            fileName = MimeUtility.decodeText(fileName);
                        }

                        return write2file(context, fileName, stream);
                        //return fileName;
                        //attachmentModels.add(new AttachmentModel(fileName, bodyPart.getInputStream()));
                    }

                }else if(bodyPart.isMimeType("multipart/*")){
                    Log.v("saveAttachmentFile" , "multipart");
                    saveAttachmentFile(context, bodyPart);
                }else{
                    fileName = bodyPart.getFileName();
                    if(fileName != null && fileName.toLowerCase().indexOf("gb2312") != -1){
                        fileName = MimeUtility.decodeText(fileName);
                        return write2file(context, fileName, stream);
                        //return fileName;
                        //attachmentModels.add(new AttachmentModel(fileName, bodyPart.getInputStream()));
                    }
                }
            }

        }else if(part.isMimeType("message/rfc822")){
            saveAttachmentFile(context,(Part) part.getContent());
        }

        return fileName;
    }

    private static String write2file(Context context,
                                     String fileName,
                                     InputStream input ) throws IOException {
        fileName = context.getFilesDir() + "/" + fileName;
        FileOutputStream output = new FileOutputStream(fileName);//context.openFileOutput(filename, Context.MODE_PRIVATE);
        byte[] buffer = new byte[4096];

        int byteRead;

        while ((byteRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, byteRead);
        }
        output.close();

        return fileName;
    }
}
