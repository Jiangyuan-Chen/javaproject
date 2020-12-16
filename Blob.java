import java.io.File;


public class Blob extends GitObject{

    public Blob(String filePath) throws Exception {
        setFile(new File(filePath));
        setName(SHA1CheckSum.FileSHA1Checksum(getFile()));
    }

    @Override
    public void write() throws Exception {
        new KeyValueStore(getFile()).writeFile();
    }
}
