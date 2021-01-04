import java.io.File;
import java.security.Key;
import java.util.Objects;


public class Tree extends GitObject {

    public Tree(String filePath) throws Exception {

        setFile(new File(filePath));
        StringBuilder str = new StringBuilder();
        for (File x : Objects.requireNonNull(getFile().listFiles())) {
            if (x.isDirectory()) {
                str.append("tree ").append(new Tree(filePath + File.separator + x.getName()).getName()).append("\t").append(x.getName()).append("\n");
            }
            if (x.isFile()) {
                str.append("Blob ").append(new Blob(filePath + File.separator + x.getName()).getName()).append("\t").append(x.getName()).append("\n");
            }
        }
        setValue(str.toString());
        setName(SHA1CheckSum.StringSHA1Checksum(getValue()));
    }

    @Override
    public void write() throws Exception {
        new KeyValueStore(getValue()).writeString();
    }

    public void write(File path) throws Exception {
        new KeyValueStore(getValue()).writeString(path);
    }

    // 把工作区的文件写到Branch里存放当前分支工作区文件的文件夹内
    public void writeTreeFiles(String path) throws Exception {
        // 获取当前所在分支名
        String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        // 递归遍历工作区里的内容并转化为blob和tree保存起来
        for (File x : Objects.requireNonNull(new File(path).listFiles())) {
            // 遇到文件夹时递归进入遍历
            if (x.isDirectory()) {
                new Tree(x.getAbsolutePath()).write(new File(new File("Branch").getAbsolutePath() + File.separator + branch + "Files"));
                writeTreeFiles(path + File.separator + x.getName());
            }
            // 把文件写到Branch里存放当前分支工作区文件的文件夹内
            if (x.isFile()) {
                new Blob(x.getAbsolutePath()).write(new File(new File("Branch").getAbsolutePath() + File.separator + branch + "Files"));
            }
        }
    }
}
