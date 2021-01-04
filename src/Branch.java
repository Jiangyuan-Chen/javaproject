import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
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
        // 新建该分支存储Commit的文件夹，文件夹名为该分支的名字+Commits，文件内容为Object Commit和commit files
        new File(new File("Branch").getAbsolutePath() + File.separator + getName() + "Commits").mkdir();
        new File(new File("Branch").getAbsolutePath() + File.separator + getName() + "Commits" + File.separator + "CommitFiles").mkdir();
    }

    // 切换分支
    public static void switchBranch(String Branch) throws Exception {
        // 把工作区的文件写到Branch里存放当前分支工作区文件的文件夹内
        new Tree("../workspace").writeTreeFiles("../workspace");
        // 获取当前所在分支名
        String beforeBranch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        Tree beforeTree = new Tree(new File("Branch").getAbsolutePath() + File.separator + beforeBranch + "Files");
        // 设置HEAD指针指向切换后的当前分支
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
            outputStreamHEAD.write(Branch.getBytes());
        }
        // 获取当前所在分支名
        String afterBranch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        Tree afterTree = new Tree(new File("Branch").getAbsolutePath() + File.separator + afterBranch + "Files");
        // 判断切换后分支工作区的内容是否和切换前分支工作区的内容是否一样
        // 不一样时才更新工作区
        if (!beforeTree.getName().equals(afterTree.getName())) {
            deleteFiles(new File("../workspace"));
            String workspace = "../workspace";
            new File(workspace).mkdir();
            String path = new File("Branch").getAbsolutePath() + File.separator + afterBranch + "Files";
            rewriteBranchFiles(afterTree, workspace, path);
        }
    }

    public static void rewriteBranchFiles(Tree afterTree, String workspace, String path) throws Exception {
        // 把切换后分支的文件内容写进工作区
        String[] v = afterTree.getValue().split(" |\n|\t");
        File[] file = new File(path).listFiles();
        for (int i = 0; i < file.length; i++) {
            if ("Blob".equals(v[3*i])) {
                new KeyValueStore(v[3*i+2],file[i]).writeFile(new File(workspace));
            }
            if ("Tree".equals(v[3*i])) {
                new File(path, v[3*i+2]).mkdir();
                path +=  File.separator + v[3*i+2];
                rewriteBranchFiles(afterTree, workspace + File.separator + v[3*i+2], path);
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
