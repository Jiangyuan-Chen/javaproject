import java.io.File;
import java.util.Objects;


public class Tree extends GitObject {

    public Tree(String filePath) throws Exception {

        File file = new File(filePath);
        StringBuilder str = new StringBuilder();
        for (File x : Objects.requireNonNull(file.listFiles())) {
            if (x.isDirectory()) {
                str.append("tree ").append(new Tree(filePath + File.separator + x.getName()).getName()).append("\t").append(x.getName()).append("\n");
            }
            if (x.isFile()) {
                str.append("Blob ").append(new Blob(filePath + File.separator + x.getName()).getName()).append("\t").append(x.getName()).append("\n");
            }
        }
        setName(SHA1CheckSum.StringSHA1Checksum(getValue()));
        setValue(str.toString());
    }

    @Override
    public void write() throws Exception {
        new KeyValueStore(getValue()).writeString();
    }
}
