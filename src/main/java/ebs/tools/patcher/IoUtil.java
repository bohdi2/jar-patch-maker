package ebs.tools.patcher;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IoUtil {

    public static void copy(InputStream in, OutputStream out) throws IOException {

        int c;
        while ((c = in.read()) != -1) {
            out.write(c);
        }

        in.close();
    }
}
