package sermk.pipi.mclient;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;

import sermk.pipi.pilib.PiUtils;

/**
 * Created by echormonov on 19.12.17.
 */

public class FilterMessage {

    private String[] froms = new String[0];
    private String[] subjects = new String[0];

    public FilterMessage(final String[] from, final String[] subject) {
        this.froms = from;
        this.subjects = subject;
    }

    public boolean checkMessage(Message msg) throws MessagingException {

        Address[] address = msg.getFrom();
        if(address==null){
            return false;
        }

        final String from = ((InternetAddress)address[0]).getAddress();
        if( !PiUtils.contains2(this.froms, from) ){
            return false;
        }

        final String subject = msg.getSubject();
        if(subject == null){
            return false;
        }
        if(subject.isEmpty()){
            return false;
        }
        /*
        if( Arrays.binarySearch(this.subjects, subject) < 0 ){
            return false;
        }
        */
        for (String subj: this.subjects) {
            if(subject.indexOf(subj) != 0){
                continue;
            }
            return true;
        }
        return false;
    }

    static public boolean equalsSubject(final String mSubj, final String refSubj){
        if(mSubj.indexOf(refSubj) == 0){
            return true;
        }
        return false;
    }

}
