import java.io.File;

public abstract class GitObject {

    private String name;
    private String value;
    private File file;


    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getName(){
        return name;
    }

    public String getValue(){
        return value;
    }

    public File getFile(){
        return file;
    }

    public abstract void write() throws Exception;
}
