import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Scanner;


public class KeyValueStore {
    private String name;
    private File file;
    private String value;

    public KeyValueStore(File file) throws Exception {
        this.file = file;
        this.name = SHA1CheckSum.FileSHA1Checksum(file);
    }

    public KeyValueStore(String value) throws Exception {
        this.value = value;
        this.name = SHA1CheckSum.StringSHA1Checksum(value);
    }


    // 在工作目录新建文件，提取源文件的内容到新文件
    public void writeFile() throws Exception {
        FileOutputStream output = new FileOutputStream(new File(name), true); // 生成名为文件哈希值的空文件并允许向value后面添加内容
        FileInputStream is = new FileInputStream(file);
        // 用于读文件的缓存区
        byte[] buffer = new byte[1024];
        int numRead;
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

    // 在工作目录新建文件，value为输入的字符串
    public void writeString() throws Exception {
        new FileOutputStream(name).write(value.getBytes());
    }

    // 给定key，在指定目录获得文件的value
    public String getValue(String key) throws Exception {
        System.out.print("请输入指定目录的路径：");
        String path = new Scanner(System.in).next();
        File dir = new File(path);
        File[] fs = dir.listFiles(); //将目录里的文件和文件夹对象放入数组fs
        StringBuilder value = new StringBuilder();
        //按顺序遍历每个对象的名字是否匹配key
        assert fs != null;
        if (fs.length == 0) return "指定路径内没有文件存在";
        for (File f : fs) {
            if (f.getName().equals(key)) {
                FileInputStream is = new FileInputStream(f);
                // 用于读文件的缓存区
                byte[] buffer = new byte[1024];
                int numRead;
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
                return value.toString();
            }
        }
        return "未发现文件：" + key;
    }

















}
