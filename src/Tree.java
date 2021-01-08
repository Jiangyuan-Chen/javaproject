import java.io.File;
import java.util.Arrays;
import java.util.Objects;


/**
 * @author chenjiangyuan
 */
public class Tree extends GitObject {

    public Tree(String filePath) throws Exception {

        setFile(new File(filePath));
        StringBuilder str = new StringBuilder();
        for (File x : Objects.requireNonNull(getFile().listFiles())) {
            if (x.isDirectory()) {
                str.append("Tree ").append(new Tree(filePath + File.separator + x.getName()).getName()).append("\t").append(x.getName()).append("\n");
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

    /**
     * 把文件转化为Blob和Tree存入文件夹Objects
     *
     * @param path 被转化为Blob和Tree的文件夹
     * @throws Exception 计算SHA1和新建Tree对象时可能会报错
     */
    public void writeTreeFiles(String path) throws Exception {
        // 获得文件夹Object里所有objects的名字
        File[] file = new File("Objects").listFiles();
        String[] objectsName =new String[file.length];
        for (int q = 0; q < objectsName.length; q++){
            objectsName[q] = file[q].getName();
        }

        // 计算工作区每个文件的哈希值
        File[] workspaceFiles = new File(path).listFiles();
        // 新建一个数组存放工作区每个文件的哈希值
        String[] fileSHA1 = new String[workspaceFiles.length];
        // 分别判断工作区每个文件的类型（文件或者文件夹），并分别用相应的方法获取哈希值
        for (int i = 0; i < workspaceFiles.length; i++){
            // 如果是文件，则用方法FileSHA1Checksum计算哈希值
            if (workspaceFiles[i].isFile()){
                fileSHA1[i] = SHA1CheckSum.FileSHA1Checksum(workspaceFiles[i]);
            }
            // 如果不是文件（即是文件夹），则对其新建Tree对象并获取它的名字即为哈希值
            else {
                fileSHA1[i] = new Tree(workspaceFiles[i].getAbsolutePath()).getName();
            }
        }

        // 递归遍历工作区里的内容并转化为blob和tree保存起来
        for (int j = 0; j < fileSHA1.length; j++) {
            if (!Arrays.asList(objectsName).contains(fileSHA1[j])){
                // 遇到文件夹时先把它转换成tree并写入文件夹Object里，然后递归进入遍历
                if (workspaceFiles[j].isDirectory()) {
                    new Tree(workspaceFiles[j].getAbsolutePath()).write(new File("Objects"));
                    writeTreeFiles(path + File.separator + workspaceFiles[j].getName());
                }
                // 遇到文件就把它转换成blob并写入文件夹Object里
                if (workspaceFiles[j].isFile()) {
                    new Blob(workspaceFiles[j].getAbsolutePath()).write(new File("Objects"));
                }
            }
        }
    }
}
