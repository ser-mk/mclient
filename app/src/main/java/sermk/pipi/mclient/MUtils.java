package sermk.pipi.mclient;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by echormonov on 23.12.17.
 */

public final class MUtils {

    final static int MAX_BYTES = 500000;
    final static String ERORR_MAX_BYTES = "limit max bytes";

    static public byte[] getByteOfFile(@NonNull final String pathNonLocalFile){

        byte[] bytes = new byte[0];

        try(FileInputStream inputStream = new FileInputStream(pathNonLocalFile)) {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        }
        catch (IOException e) {
            //e.printStackTrace();
        }

        if(bytes.length > MAX_BYTES){
            bytes = ERORR_MAX_BYTES.getBytes();
            Log.v(ERORR_MAX_BYTES, "!!!");
        }

        return bytes;
    }

    static public String md5(String source) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(source.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                /*
                hexString.append(Integer.
                        toHexString((0xFF & messageDigest[i]) + 0x100)); */
                hexString.append(Integer.toString(
                        ( messageDigest[i] & 0xff ) + 0x100,
                        16).substring( 1 ));
            return hexString.toString();
        }catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

}
