import java.io.File;


public class Blob extends GitObject{

    private File file;


    public Blob(String filePath) throws Exception {
        this.file = new File(filePath);
        setName(SHA1CheckSum.FileSHA1Checksum(file));
    }

    @Override
    // 生成文件到工作目录并哈希
    public void write() throws Exception {
        new KeyValueStore(file).writeFile();
    }

    public File getFile(){
        return file;
    }
}
