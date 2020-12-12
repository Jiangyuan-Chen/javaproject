public abstract class GitObject {

    private String name;


    public void setName(String name) {
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public abstract void write() throws Exception;
}
