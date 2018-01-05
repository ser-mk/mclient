package sermk.pipi.mlib;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

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
}
