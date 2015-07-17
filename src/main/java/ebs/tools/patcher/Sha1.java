
package ebs.tools.patcher;

import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Formatter;

public class Sha1 {

    public static String calculateHash(InputStream stream) throws Exception {

        MessageDigest sha1 = MessageDigest.getInstance("SHA1");
        return  calculateHash(sha1, stream);
    }


    private static String calculateHash(MessageDigest algorithm, InputStream stream) throws Exception {

        DigestInputStream dis = new DigestInputStream(stream, algorithm);

        // read the file and update the hash calculation
        while (dis.read() != -1)
            ;

        // get the hash value as byte array
        byte[] hash = algorithm.digest();

        return byteArray2Hex(hash);
    }

    private static String byteArray2Hex(byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }

}