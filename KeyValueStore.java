import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


public class KeyValueStore {

    // 生成文件到工作目录并哈希
    public void WriteFile(String sourceFile) throws Exception {
        File file = new File(sourceFile);
        FileInputStream is = new FileInputStream(file);
        FileOutputStream output = new FileOutputStream(SHA1CheckSum.FileSHA1Checksum(new FileInputStream(file)), true); // 生成名为文件哈希值的空文件并允许向value后面添加内容
        // 用于读文件的缓存区
        byte[] buffer = new byte[1024];
        int numRead = 0;
        do {
            numRead = is.read(buffer); // 读出numRead字节到buffer中
            if (numRead > 0) {
                output.write(buffer); // 把buffer中的内容写入文件
            }
            // 文件已读写完，退出循环
        } while (numRead != -1);
        // 关闭输入输出流
        is.close();
        output.close();
    }

    // 在工作目录新建文件，value为输入的字符串，key为文本文档的哈希值
    public void WriteString(String s) throws Exception {
        new FileOutputStream(SHA1CheckSum.StringSHA1Checksum(s)).write(s.getBytes());
    }

    // 给定key，在工作目录获得文件的value
    public String getValue(String key) throws Exception {
        File dir = new File(System.getProperty("user.dir"));
        File[] fs = dir.listFiles(); //将目录里的文件和文件夹对象放入数组fs
        StringBuilder value = new StringBuilder();
        //按顺序遍历每个对象的名字是否匹配key
        if (fs == null) throw new AssertionError();
        for (File f : fs) {
            if (f.getName().equals(key)) {
                FileInputStream is = new FileInputStream(f);
                // 用于读文件的缓存区
                byte[] buffer = new byte[1024];
                int numRead = 0;
                do {
                    // 读出numRead字节到buffer中
                    numRead = is.read(buffer);
                    if (numRead > 0) {
                        value.append(new String(buffer)); // 把buffer中的字节转换成字符串后再将其附加在value之后
                    }
                    // 文件已读完，退出循环
                } while (numRead != -1);
                // 关闭输入流
                is.close();
            }
        }
        return value.toString();
    }

    // 给定key，在指定目录获得文件的value
    public String getValue(String key, String path) throws Exception {
        File dir = new File(path);
        File[] fs = dir.listFiles(); //将目录里的文件和文件夹对象放入数组fs
        StringBuilder value = new StringBuilder();
        //按顺序遍历每个对象的名字是否匹配key
        if (fs == null) throw new AssertionError();
        for (File f : fs) {
            if (f.getName().equals(key)) {
                FileInputStream is = new FileInputStream(f);
                // 用于读文件的缓存区
                byte[] buffer = new byte[1024];
                int numRead = 0;
                do {
                    // 读出numRead字节到buffer中
                    numRead = is.read(buffer);
                    if (numRead > 0) {
                        value.append(new String(buffer));// 把buffer中的字节转换成字符串后再将其附加在value之后
                    }
                    // 文件已读完，退出循环
                } while (numRead != -1);
                // 关闭输入流
                is.close();
            }
        }
        return value.toString();
    }















}
