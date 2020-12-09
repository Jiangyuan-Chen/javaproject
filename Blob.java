import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class Blob {
    private String name;
    private FileInputStream is;
    private FileOutputStream output;

    public Blob(String filePath) throws Exception {
        this.name = SHA1CheckSum.FileSHA1Checksum(new FileInputStream(new File(filePath)));
        this.is = new FileInputStream(new File(filePath));
    }

    public void writeBlob() throws Exception {
        output = new FileOutputStream(new File(name));
        byte[] buffer = new byte[1024];
        int numRead = 0;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                output.write(buffer); //把buffer中的字节写入新建的文件
            }
            // 文件已读完，退出循环
        } while (numRead != -1);
        // 关闭输入输出流
        is.close();
        output.close();
    }

    public String getName(){
        return name;
    }
}
