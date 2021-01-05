import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class Commit extends GitObject{

    private Tree workTree;

    public Commit() throws Exception {
        String str = "";
        setWorkTree(new Tree("../workspace"));
        // HEAD存在时即不是第一次commit
        try {
            String HEAD = KeyValueStore.readFileString("HEAD");
            str = "Tree " + workTree.getName() + "\n" + "Parent " + HEAD;
        } // HEAD不存在即第一次commit
        catch (FileNotFoundException e){
            str = "Tree " + workTree.getName();
        } // 设置commit的value和name
        finally {
            setValue(str);
            setName(SHA1CheckSum.StringSHA1Checksum(getValue()));
        }
    }


    @Override
    public void write() throws Exception {
        // 默认GitObjects存放在工作目录
        String newTreeKey = "";
            try {
                // 最新commit的key
                String HEAD = KeyValueStore.readFileString("HEAD");
                // 取出最新commit上传的tree name
                String value = KeyValueStore.readFileString(HEAD);
                String[] v = value.split(" |\n");
                newTreeKey = v[1];
            } catch (FileNotFoundException ignored){}
        if (workTree.getName().equals(newTreeKey)) {
            System.out.println("tree相同，commit生成失败");
        }
        else {
            // 在文件夹Objects里存储本次commit的信息文件
            new KeyValueStore(getValue()).writeString(new File("Objects"));
            // 在文件夹Objects里存储本次commit工作区的tree
            new Tree("../workspace").write(new File("Objects"));
            // 更新工作目录里HEAD的指针，令其存储本次最新的commit key
            writeHEAD();
            // 更新文件夹Branch里当前分支的指针，令其存储本次最新的commit key
            new KeyValueStore(KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD"), getName()).writeString(new File("Branch"));
            // 把工作区的文件转化为Blob和Tree存入文件夹Objects
            new Tree("../workspace").writeTreeFiles("../workspace");
            // 把本次commit的key写入当前分支的CommitHistory里
            String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
            File totalCommitKey = new File(new File("Branch").getAbsolutePath() + File.separator + branch + "CommitHistory");
            try (FileOutputStream outputStream = new FileOutputStream(totalCommitKey, true)){
                outputStream.write((getName() + "\n").getBytes());
            }
        }


    }

    public void setWorkTree(Tree workTree) {
        this.workTree = workTree;
    }

    public Tree getWorkTree() {
        return workTree;
    }

    /**
     * 存放最新commit的key
     */
    private void writeHEAD() throws Exception {
        try (FileOutputStream outputStream = new FileOutputStream("HEAD"))
        { outputStream.write(getName().getBytes()); }
    }
}