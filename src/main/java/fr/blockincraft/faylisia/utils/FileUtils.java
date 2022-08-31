package fr.blockincraft.faylisia.utils;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
    /**
     * This method calculate SHA-1 of a {@link File}
     * @param file file which we want the SHA-1
     * @return SHA-1 of the file
     * @throws IOException if we can't create a {@link FileInputStream}
     * @throws NoSuchAlgorithmException if SHA-1 algorithm wasn't found
     */
    @Nonnull
    public static byte[] calcSHA1(@Nonnull File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream fileInputStream = new FileInputStream(file);
        MessageDigest digest = MessageDigest.getInstance("SHA-1");
        DigestInputStream digestInputStream = new DigestInputStream(fileInputStream, digest);
        byte[] bytes = new byte[1024];
        // Read all file content
        while (digestInputStream.read(bytes) > 0);

        byte[] resultByteArray = digest.digest();
        digestInputStream.close();
        return resultByteArray;
    }

    /**
     * @return server resource pack as {@link File}
     */
    @Nonnull
    public static File getResourcePack() {
        return new File("resource_pack.zip");
    }
}
