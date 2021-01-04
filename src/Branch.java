import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.security.Key;
import java.util.Objects;
import java.util.TreeMap;

public class Branch extends GitObject{

    public Branch(String name) throws Exception {
        setName(name);
        try {setValue(KeyValueStore.readFileString("HEAD"));}
        catch (FileNotFoundException e) {setValue("None");}
        File dir = new File("Branch");
        if (!dir.exists()){
            dir.mkdir();
        }
        File obj = new File("Objects");
        if (!obj.exists()){
            obj.mkdir();
        }
    }


    @Override
    public void write() throws Exception {
        new KeyValueStore(getName(), getValue()).writeBranch();
        // 添加新建分支的名字到Branch list里
        try (FileOutputStream outputStreamBranches = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "Branch List.txt", true)) {
            outputStreamBranches.write((getName() + "\n").getBytes());
        }
        // 设置HEAD指针指向当前分支
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
             outputStreamHEAD.write(getName().getBytes());
        }
    }

    // 切换分支
    public static void switchBranch(String Branch) throws Exception {
        // 设置HEAD指针指向切换后的当前分支
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
            outputStreamHEAD.write(Branch.getBytes());
        }
        String branchCommitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + Branch);
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + branchCommitKey);
        String[] v = commitValue.split(" |\n");
        String treeKey = v[1];
        final String treePath = new File("Objects").getAbsolutePath();

        deleteFiles(new File("../workspace"));
        new File("../workspace").mkdir();
        rewriteBranchFiles(treePath, treeKey, "../workspace");

    }

    public static void rewriteBranchFiles(String treePath, String treeKey, String path) throws Exception {
        // 把切换后分支的文件内容写进工作区
        String treeValue = KeyValueStore.readFileString(treePath + File.separator + treeKey);
        String[] v = treeValue.split(" |\n|\t");

        for (int i = 0; i < v.length/3; i++) {
            if ("Blob".equals(v[3*i])) {
                File blob = new File(new File("Objects").getAbsolutePath() + File.separator + v[3*i+1]);
                new KeyValueStore(v[3*i+2], blob).writeFile(new File(path));
            }
            if ("Tree".equals(v[3*i])) {
                new File(path, v[3*i+2]).mkdir();
                path += File.separator + v[3*i+2];
                //String insideTreeValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + v[3*i+1]);
                //String[] value = insideTreeValue.split(" |\n|\t");
                treeKey = v[3*i+1];
                rewriteBranchFiles(treePath, treeKey, path);
            }
        }
    }

    public static void deleteFiles(File file){
        if (file.isDirectory()) {
            File[] files=file.listFiles();
            assert files != null;
            for (File value : files) {
                if (value.isDirectory()) {
                    deleteFiles(value);
                } else {
                    value.delete();
                }
            }
        }
        file.delete();
    }
}
