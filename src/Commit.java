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
            str = "tree " + workTree.getName() + "\n" + "parent " + HEAD;
        } // HEAD不存在即第一次commit
        catch (FileNotFoundException e){
            str = "tree " + workTree.getName();
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
            new KeyValueStore(getValue()).writeString(new File(new File("Branch").getAbsolutePath() + File.separator + getName() + "Commits"));
            writeHEAD();
            new KeyValueStore(KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD"), getName()).writeString(new File("Branch"));







        }


    }

    public void setWorkTree(Tree workTree) {
        this.workTree = workTree;
    }

    public Tree getWorkTree() {
        return workTree;
    }

    /** 存放最新commit的key */
    private void writeHEAD() throws Exception {
        try (FileOutputStream outputStream = new FileOutputStream("HEAD"))
        { outputStream.write(getName().getBytes()); }
    }
}