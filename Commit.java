import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;


public class Commit extends GitObject{

    public Commit(String filePath) throws Exception {
        setFile(new File(filePath));
        String str = "";
        // HEAD存在时即不是第一次commit
        try {
            String HEAD = KeyValueStore.readFileString("HEAD");
            str = "tree " + getFile().getName() + "\n" + "parent " + HEAD;
        } // HEAD不存在即第一次commit
        catch (FileNotFoundException e){
            str = "tree " + getFile().getName();
        } // 设置commit的value和name
        finally {
            setValue(str);
            setName(SHA1CheckSum.StringSHA1Checksum(getValue()));
        }
    }


    @Override
    public void write() throws Exception {
        // 默认GitObjects存放在工作目录
        File dir = new File(System.getProperty("user.dir"));
        File[] fs = dir.listFiles();
        String newTreeKey = "";
        for (File f : fs) {
            try {
                // 最新commit的key
                String HEAD = KeyValueStore.readFileString("HEAD");
                if (HEAD.equals(f.getName())) {
                    String value = KeyValueStore.readFileString(f.getName());
                    String[] v = value.split(" |\n");
                    newTreeKey = v[1];
                    break;
                }
            } catch (FileNotFoundException e){
                break;
            }
        }
        if (getFile().getName().equals(newTreeKey)){
            System.out.println("tree相同，commit生成失败");
        }
        else {
            new KeyValueStore(getValue()).writeString();
            writeHEAD();
        }
    }

    /** 存放最新commit的key */
    private void writeHEAD() throws Exception {
        try (FileOutputStream outputStream = new FileOutputStream("HEAD"))
        { outputStream.write(getName().getBytes()); }
    }
}







