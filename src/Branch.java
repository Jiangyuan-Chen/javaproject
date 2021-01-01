import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Branch extends GitObject{

    public Branch(String name) throws Exception {
        setName(name);
        try {setValue(KeyValueStore.readFileString("HEAD"));}
        catch (FileNotFoundException e) {setValue("None");}
        File dir = new File("Branch");
        if (!dir.exists()){
            dir.mkdir();
        }
    }


    @Override
    public void write() throws Exception {
        new KeyValueStore(getName(), getValue()).writeBranch();
        try (FileOutputStream outputStreamBranches = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "Branch List.txt", true)) {
            outputStreamBranches.write((getName() + "\n").getBytes());
        }
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
             outputStreamHEAD.write(getName().getBytes());
        }
    }
}
