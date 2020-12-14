import java.io.File;
import java.util.Objects;
import java.util.Scanner;


public class UnitTest2 {
    public static void main(String[] args) throws Exception {
        System.out.print("请输入要被转化的文件夹目录：");
        File file = new File(new Scanner(System.in).next());
        for (File x : Objects.requireNonNull(file.listFiles())){
            if (x.isFile()) {
                new Blob(x.getAbsolutePath()).write();
            }
            if (x.isDirectory()) {
                new Tree(x.getAbsolutePath()).write();
            }
        }
        System.out.println("转化成功，请到工作目录查看。");
    }
}
