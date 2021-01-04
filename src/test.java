import java.io.File;

public class test {
    public static void main(String[] args) throws Exception {
        //new Commit().write();
        //new Branch("test1").write();
        deleteFiles(new File("/Users/chenjiangyuan/软工课程资料/Java/作业/github/javaproject/aaa"));
        System.out.println("成功");
    }

    public static void deleteFiles(File file){
        if (file.isDirectory()) {
            File[] files=file.listFiles();
            assert files != null;
            for (File value : files) {
                if (value.isDirectory()) {
                    deleteFiles(value);
                } else {
                    value.delete();
                }
            }
        }
        file.delete();
    }


}
