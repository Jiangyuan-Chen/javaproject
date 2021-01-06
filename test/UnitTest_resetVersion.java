import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class UnitTest_resetVersion {

    //目标路径
    public static String target = "/Users/chenjiangyuan/软工课程资料/Java/作业/github/workspace";

    /**
     * 主函数
     * @param args
     */
    public static void main(String[] args) throws Exception {
        new Branch("main").write();
        Clone("/Users/chenjiangyuan/软工课程资料/Java/作业/github/aaa");
        new Commit().write();
        Clone("/Users/chenjiangyuan/软工课程资料/Java/作业/github/bbb");
        new Commit().write();
        Clone("/Users/chenjiangyuan/软工课程资料/Java/作业/github/ccc");
        new Commit().write();
        Branch.commitList();

    }

    /**
     * 遍历文件夹并复制
     */
    public static void Clone(String url){
        //获取目录下所有文件
        File f = new File(url);
        File[] allf = f.listFiles();

        //遍历所有文件
        for(File fi:allf) {
            try {
                //拼接目标位置
                String URL = target+fi.getAbsolutePath().substring("/Users/chenjiangyuan/软工课程资料/Java/作业/github/aaa".length());

                //创建目录或文件
                if(fi.isDirectory()) {
                    Createflies(URL);
                }else {
                    fileInputOutput(fi.getAbsolutePath(),URL);
                }

                //递归调用
                if(fi.isDirectory()) {
                    Clone(fi.getAbsolutePath());
                }

            }catch (Exception e) {
                System.out.println("error");
            }
        }
    }

    /**
     * 复制文件
     */
    public static void fileInputOutput(String sourse,String target) {
        try {
            File s = new File(sourse);
            File t = new File(target);

            FileInputStream fin = new FileInputStream(s);
            FileOutputStream fout = new FileOutputStream(t);

            byte[] a = new byte[1024*1024*4];
            int b = -1;

            //边读边写
            while((b = fin.read(a))!=-1) {
                fout.write(a,0,b);
            }

            fout.close();
            fin.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建目录
     * @param name
     * @return
     */
    public static boolean Createflies(String name) {
        boolean flag=false;
        File file=new File(name);
        //创建目录
        if(file.mkdir() == true){
            flag=true;
        }else {
            flag=false;
        }

        return flag;
    }

}
