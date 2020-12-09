import java.io.InputStream;
import java.security.MessageDigest;


public class SHA1CheckSum {
    // 计算文件的哈希值
    public static String FileSHA1Checksum(InputStream is) throws Exception {
        // 用于计算hash值的文件缓冲区
        byte[] buffer = new byte[1024];
        // 使用SHA1哈希/摘要算法
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
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
        return HexadecimalHash(complete);
    }

    // 计算字符串的哈希值
    public static String StringSHA1Checksum(String value) throws Exception{
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        complete.update(value.getBytes());
        return HexadecimalHash(complete);
    }

    // 计算SHA1的十六进制的哈希值
    private static String HexadecimalHash(MessageDigest complete){
        byte[] sha1 = complete.digest();
        String result = "";
        for (int i = 0; i < sha1.length; i++) {
            result += Integer.toString(sha1[i]&0xFF, 16);
        }
        return result;
    }
}
