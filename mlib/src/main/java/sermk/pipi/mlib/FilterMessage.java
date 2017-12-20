package sermk.pipi.mlib;

import java.io.IOException;
import java.util.Arrays;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;

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
        if( Arrays.binarySearch(this.froms, from) < 0 ){
            return false;
        }

        final String subject = msg.getSubject();
        if( Arrays.binarySearch(this.subjects, subject) < 0 ){
            return false;
        }
        return true;
    }

}