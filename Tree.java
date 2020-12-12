import java.io.File;
import java.util.Objects;


public class Tree extends GitObject{

    private String value = "";


    public Tree(String filePath) throws Exception {
        File file = new File(filePath);
        for (File x : Objects.requireNonNull(file.listFiles())) {
            if (x.isDirectory())
                value += "tree " + new Tree(filePath + File.separator + x.getName()).getName() + "\t" + x.getName() + "\n";
            if (x.isFile())
                value += "Blob " + new Blob(filePath + File.separator + x.getName()).getName() + "\t" + x.getName() + "\n";
        }
        setName(SHA1CheckSum.StringSHA1Checksum(value));
    }

    @Override
    // 在工作目录新建文件，value为输入的字符串，key为文本文档的哈希值
    public void write() throws Exception {
        new KeyValueStore(getName(), value).writeString();
    }

    public String getValue(){
        return value;
    }
}
