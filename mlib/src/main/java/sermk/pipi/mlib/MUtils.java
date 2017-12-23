package sermk.pipi.mlib;

import android.support.annotation.NonNull;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by echormonov on 23.12.17.
 */

public final class MUtils {

    static public byte[] getByteOfFile(@NonNull final String pathNonLocalFile){

        byte[] bytes = new byte[0];

        try(FileInputStream inputStream = new FileInputStream(pathNonLocalFile)) {
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }
}
