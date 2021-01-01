import java.io.File;

public class test {
    public static void main(String[] args) throws Exception {
        new Commit(System.getProperty("user.dir") + File.separator + "ccc").write();
        //new Branch("test1").write();

        System.out.println("成功");
    }
}
