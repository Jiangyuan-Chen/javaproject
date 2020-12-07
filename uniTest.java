import java.util.Scanner;

public class uniTest {
    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        System.out.print("请输入指定目录：");
        String path = input.next();
        SHA1CheckSum sha1 = new SHA1CheckSum(path);

        // 输入key查找value
        System.out.print("请输入key：");
        String hash = input.next();
        sha1.getValue(hash);

        // 移动文件到指定目录并哈希
        System.out.print("请输入要移动的文件目录：");
        String sourceFile = input.next();
        sha1.WriteFile(sourceFile);

        // 新建文本文档，value为输入的字符串
        System.out.print("请输入新建文本文档的value：");
        String s = input.next();
        sha1.WriteString(s);
    }
}
