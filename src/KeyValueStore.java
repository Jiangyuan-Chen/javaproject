import java.io.*;
import java.util.Scanner;


/**
 * @author chenjiangyuan
 */
public class KeyValueStore {
    private String name;
    private File file;
    private String value;

    public KeyValueStore(File file) throws Exception {
        this.file = file;
        this.name = SHA1CheckSum.FileSHA1Checksum(file);
    }

    public KeyValueStore(String name, File file) {
        this.file = file;
        this.name = name;
    }

    public KeyValueStore(String value) throws Exception {
        this.value = value;
        this.name = SHA1CheckSum.StringSHA1Checksum(value);
    }

    public KeyValueStore(String name, String value) {
        this.value = value;
        this.name = name;
    }


    /**
     * 在工作目录新建文件，提取源文件的内容到新文件
     *
     * @throws IOException FileOutputStream和FileInputStream可能会抛出IOException
     */
    public void writeFile() throws IOException {
        // 生成名为文件哈希值的空文件并允许向value后面添加内容
        FileOutputStream output = new FileOutputStream(new File(name), true);
        FileInputStream is = new FileInputStream(file);
        // 用于读文件的缓存区
        byte[] buffer = new byte[1024];
        int numRead;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                // 把buffer中的内容写入文件
                output.write(buffer);
            }
            // 文件已读写完，退出循环
        } while (numRead != -1);
        // 关闭输入输出流
        is.close();
        output.close();
    }

    /**
     * 在指定目录新建文件，提取源文件的内容到新文件
     *
     * @param path 指定新建文件的目录
     * @throws IOException FileOutputStream和FileInputStream可能会抛出IOException
     */
    public void writeFile(File path) throws IOException {
        // 生成名为文件哈希值的空文件并允许向value后面添加内容
        File f = new File(path, name);
        FileOutputStream output = new FileOutputStream(f, true);
        FileInputStream is = new FileInputStream(file);
        // 用于读文件的缓存区
        byte[] buffer = new byte[1024];
        int numRead;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                // 把buffer中的内容写入文件
                output.write(buffer);
            }
            // 文件已读写完，退出循环
        } while (numRead != -1);
        // 关闭输入输出流
        is.close();
        output.close();
    }

    /**
     * 在工作目录新建文件，value为文件的字符串，name为文件的名字
     *
     * @throws IOException FileOutputStream可能会抛出IOException
     */
    public void writeString() throws IOException {
        try(FileOutputStream outputStream = new FileOutputStream(name)){
            outputStream.write(value.getBytes());
        }
    }

    /**
     * 在指定目录新建文件，value为文件的字符串，name为文件的名字
     *
     * @param path 指定新建文件的目录
     * @throws IOException FileOutputStream可能会抛出IOException
     */
    public void writeString(File path) throws IOException {
        File file = new File(path, name);
        try(FileOutputStream outputStream = new FileOutputStream(file)){
            outputStream.write(value.getBytes());
        }
    }

    /**
     * 在文件夹Branch里新建分支文件，文件名为创建的分支名，内容为当前分支最新的commit key
     *
     * @throws IOException FileOutputStream可能会抛出IOException
     */
    public void writeBranch() throws IOException {
        try(FileOutputStream outputStream = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + name)){
        outputStream.write(value.getBytes());
        }
    }

    /**
     * 根据文件名自动查找到工作目录下的文件，并读取文件中的字符串
     *
     * @param filename 被读取的文件
     * @return 返回被读取的文件内的字符串
     * @throws IOException FileInputStream可能会抛出IOException
     */
    public static String readFileString(String filename) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename));
        StringBuilder fileString = new StringBuilder();
        // 用于读文件的缓存区
        byte[] buffer = new byte[1024];
        int numRead;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                /*
                 * 把buffer中的内容写入文件
                 * String() 这个方法里的参数 (0，numRead) 十分重要！！可以把数组后面的null去掉！！
                 * 浪费了我一天半的时间 = =
                 */
                fileString.append(new String(buffer, 0, numRead));
            }
            // 文件已读写完，退出循环
        } while (numRead != -1);
        // 关闭输入输出流
        is.close();
        return fileString.toString();
    }

    /**
     * 给定key，在指定目录获得文件的value
     *
     * @param key 被查找value的文件的key（文件名）
     * @return 被查找value的文件的value（文件内容）
     * @throws IOException FileOutputStream和FileInputStream可能会抛出IOException
     *                     文件内容读完后会抛出NegativeArraySizeException
     *                     输入的key不存在时会抛出FileNotFoundException
     */
    public String getFileValue(String key) throws IOException {
        System.out.print("请输入指定目录的路径：");
        String path = new Scanner(System.in).next();
        StringBuilder value = new StringBuilder();
        // 在指定目录里找文件名字是否匹配key
        try {
            FileInputStream is = new FileInputStream(path + File.separator + key);
            // 用于读文件的缓存区
            byte[] buffer = new byte[1024];
            int numRead;
            do {
                // 读出numRead字节到buffer中
                numRead = is.read(buffer);
                if (numRead == 1024) {
                    // 把buffer中的字节转换成字符串后再将其附加在value之后
                    value.append(new String(buffer));
                }
                else {
                    try{
                        byte[] newBuffer = new byte[numRead];
                        System.arraycopy(buffer, 0, newBuffer, 0, numRead);
                        value.append(new String(newBuffer));
                    } catch (NegativeArraySizeException ignored) {}
                }
                // 文件已读完，退出循环
            } while (numRead != -1);
            // 关闭输入流
            is.close();
        } catch (FileNotFoundException e) {
            return "未发现文件：" + key;
        }
        return value.toString();
    }

    public String getName(){
        return name;
    }

    public File getFile(){
        return file;
    }

    public String getValue(){
        return value;
    }
}