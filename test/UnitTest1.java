import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;


/**
 * @author chenjiangyuan
 */
public class UnitTest1 {
    public static void main(String[] args) throws Exception {

        // 自动生成测试文件夹和测试文件
        File test = new File(System.getProperty("user.dir") + File.separator + "aaa" + File.separator + "bbb" + File.separator + "ccc");
        test.mkdirs(); // 生成测试文件夹
        File intest = new File(test.getAbsolutePath() + File.separator + "ddd.txt");
        try {
            // 创建文件
            intest.createNewFile(); // 生成测试文件：ddd.txt
        } catch (Exception e1){
            e1.printStackTrace();
        }
        try {
            String s = "Congratulation on WriteFile()!";
            FileOutputStream outputStream = new FileOutputStream(intest);
            // 向文件内写入字符串s的字节数组
            outputStream.write(s.getBytes());
            outputStream.close(); // 关闭输出流
        } catch (Exception e2){
            e2.printStackTrace();
        }

        // 测试WriteFile()
        KeyValueStore kvs1 = new KeyValueStore(intest);
        kvs1.writeFile();
        System.out.println("WriteFile()执行成功");

        // 测试WriteString()
        KeyValueStore kvs2 = new KeyValueStore("Congratulation on WriteString()!");
        kvs2.writeString();
        System.out.println("WriteString()执行成功");

        // 测试getValue(String key)
        System.out.println("开始测试getValue(String key)");
        String value = kvs2.getFileValue(kvs2.getName());
        if (value.equals(kvs2.getValue())){
            System.out.println("value正确，getValue(String key)执行成功");
        }
    }
}
