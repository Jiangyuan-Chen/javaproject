import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * @author chenjiangyuan
 */
public class UnitTest_resetVersion {

    //目标路径
    public static String target = "../workspace";

    public static void main(String[] args) throws Exception {
        new File("../ddd").mkdir();
        new File("../ddd" + File.separator + "ddd.txt").createNewFile();
        try(FileOutputStream outputStream = new FileOutputStream(new File("../ddd" + File.separator + "ddd.txt"))){outputStream.write("ddd".getBytes());}
        new File("../eee").mkdir();
        try(FileOutputStream outputStream = new FileOutputStream(new File("../eee" + File.separator + "eee.txt"))){outputStream.write("eee".getBytes());}
        new File("../fff").mkdir();
        try(FileOutputStream outputStream = new FileOutputStream(new File("../fff" + File.separator + "fff.txt"))){outputStream.write("fff".getBytes());}

        new Branch("main").write();
        Clone("../ddd");
        new Commit().write();
        new Branch("test").write();
        Branch.switchBranch("test");
        Clone("../eee");
        new Commit().write();
        Clone("../fff");
        new Commit().write();
        Branch.commitList();
        String[] commitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "testCommitHistory").split("\n");
        String firstCommitKey = commitKey[0];
        Branch.resetVersion(firstCommitKey);

        File[] file = new File("../workspace").listFiles();
        File ddd = file[0];
        File dddd = new File("../ddd/ddd.txt");
        if (dddd.getName().equals(ddd.getName())){
            System.out.println("回滚成功");
        }
        else {
            System.out.println("回滚失败");
        }
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
                String URL = target+fi.getAbsolutePath().substring(fi.getAbsolutePath().length() - 8);

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
