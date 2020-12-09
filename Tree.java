import java.io.File;
import java.io.FileOutputStream;


public class Tree {
    private String name;
    private String value = "";

    public Tree(String filePath) throws Exception {
        File file = new File(filePath);
        for (File x : file.listFiles()) {
            if (x.isDirectory())
                value += "tree " + new Tree(filePath + File.separator + x.getName()).getName() + "\t" + x.getName() + "\n";
            if (x.isFile())
                value += "Blob " + new Blob(filePath + File.separator + x.getName()).getName() + "\t" + x.getName() + "\n";
        }
        name = SHA1CheckSum.StringSHA1Checksum(value);
    }

    public void writeTree() throws Exception {
        new FileOutputStream(name).write(value.getBytes());
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }
}
