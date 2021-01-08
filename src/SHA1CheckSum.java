import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


/**
 * @author chenjiangyuan
 */
public class SHA1CheckSum {
    /**
     * 计算文件的哈希值
     *
     * @param file 被计算哈希值的文件
     * @return 十六进制哈希值
     * @throws IOException FileInputStream可能会抛出IOException
     *                     方法getInstance可能会抛出NoSuchAlgorithmException
     */
    public static String FileSHA1Checksum(File file) throws IOException, NoSuchAlgorithmException {
        FileInputStream is = new FileInputStream(file);
        // 用于计算hash值的文件缓冲区
        byte[] buffer = new byte[1024];
        // 使用SHA1哈希/摘要算法
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                // 根据buffer[0:numRead]的内容更新hash值
                complete.update(buffer, 0, numRead);
            }
            // 文件已读完，退出循环
        } while (numRead != -1);
        // 关闭输入流
        is.close();
        return hexadecimalHash(complete);
    }

    /**
     * 计算字符串的哈希值
     *
     * @param value 被计算哈希值的字符串
     * @return 十六进制哈希值
     * @throws NoSuchAlgorithmException 方法getInstance可能会抛出NoSuchAlgorithmException
     */
    public static String StringSHA1Checksum(String value) throws NoSuchAlgorithmException{
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        complete.update(value.getBytes());
        return hexadecimalHash(complete);
    }

    /**
     * 计算SHA1的十六进制的哈希值
     *
     * @param complete MessageDigest
     * @return 十六进制哈希值
     */
    private static String hexadecimalHash(MessageDigest complete){
        byte[] sha1 = complete.digest();
        StringBuilder result = new StringBuilder();
        for (byte b : sha1) {
            result.append(Integer.toString((b >> 4) & 0x0F, 16)).append(Integer.toString(b & 0x0F, 16));
        }
        return result.toString();
    }
}
