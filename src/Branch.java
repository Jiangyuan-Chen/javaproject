import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;


public class Branch extends GitObject{

    public Branch(String name) throws Exception {
        setName(name);
        try {setValue(KeyValueStore.readFileString("HEAD"));}
        catch (FileNotFoundException e) {setValue("None");}
        // 创建存放分支信息的文件夹
        File dir = new File("Branch");
        if (!dir.exists()){
            dir.mkdir();
        }
        // 创建存放Object的文件夹
        File obj = new File("Objects");
        if (!obj.exists()){
            obj.mkdir();
        }
        // 在工作目录的上级目录中创建工作区
        File workspace = new File("../workspace");
        if (!workspace.exists()){
            workspace.mkdir();
        }
    }


    @Override
    public void write() throws Exception {
        // 判断新建的分支名是否和已创建的分支名重合
        try{
            String totalBranches = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "Branch List.txt");
            // 如果新建的分支名是否和已创建的分支名重合，则不创建新分支
            if (totalBranches.contains(getName())){
                return;
            }
        }catch (FileNotFoundException ignored){}



        new KeyValueStore(getName(), getValue()).writeBranch();
        // 添加新建分支的名字到Branch list里
        try (FileOutputStream outputStreamBranches = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "Branch List.txt", true)) {
            outputStreamBranches.write((getName() + "\n").getBytes());
        }

        // 新建文件记录新建分支commit的历史记录
        File commitHistory = new File(new File("Branch").getAbsolutePath() + File.separator + getName() + "CommitHistory");
        try(FileOutputStream outputStream = new FileOutputStream(commitHistory)){
            // 第一次创建分支对时候HEAD还没创建会报错，因此需要try-catch
            try{
                // 切换前所在分支的名字
                String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
                // 切换前所在分支最新的commit key
                String branchCommitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + branch);
                // 如果切换前的分支有提交过commit，则把这些记录写入新分支的记录中
                if (!"None".equals(branchCommitKey)){
                    outputStream.write(branchCommitKey.getBytes());
                }
            } catch (FileNotFoundException ignored){}
        }

        // 设置HEAD指针指向当前分支
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
            outputStreamHEAD.write(getName().getBytes());
        }
    }

    /**
     * 切换分支
     *
     * @param Branch 要切换到的分支名
     * @throws Exception readFileString()可能会抛出FileNotFoundException，FileOutputStream可能会抛出IOException
     */
    public static void switchBranch(String Branch) throws Exception {
        // 设置HEAD指针指向切换后的当前分支
        try (FileOutputStream outputStreamHEAD = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + "HEAD")) {
            outputStreamHEAD.write(Branch.getBytes());
        }
        // 切换后分支最新的commit key
        String branchCommitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + Branch);
        // 切换后分支最新的commit里的value
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + branchCommitKey);
        String[] v = commitValue.split(" |\n");
        // 获得切换后分支最新的commit里根目录的key
        String treeKey = v[1];
        final String treePath = new File("Objects").getAbsolutePath();
        // 删除工作目录后再重新创建
        deleteFiles(new File("../workspace"));
        new File("../workspace").mkdir();
        // 把切换后分支的文件内容递归写进工作区
        rewriteBranchFiles(treePath, treeKey, "../workspace");
        // 更新工作目录里HEAD的指针，令其指向本次最新的commit key
        try (FileOutputStream outputStream = new FileOutputStream("HEAD"))
        { outputStream.write(branchCommitKey.getBytes()); }
    }

    /**
     * 把切换后分支的文件内容写进工作区
     *
     * @param treePath 文件夹Object的绝对路径
     * @param treeKey 切换后分支最新的commit里根目录的key开始往下递归
     * @param path 写文件的目录从工作区目录开始往下递归
     * @throws Exception readFileString()可能会抛出FileNotFoundException
     */
    private static void rewriteBranchFiles(String treePath, String treeKey, String path) throws Exception {

        String treeValue = KeyValueStore.readFileString(treePath + File.separator + treeKey);
        String[] v = treeValue.split(" |\n|\t");

        for (int i = 0; i < v.length/3; i++) {
            if ("Blob".equals(v[3*i])) {
                File blob = new File(new File("Objects").getAbsolutePath() + File.separator + v[3*i+1]);
                new KeyValueStore(v[3*i+2], blob).writeFile(new File(path));
            }
            if ("Tree".equals(v[3*i])) {
                new File(path, v[3*i+2]).mkdir();
                rewriteBranchFiles(treePath, v[3*i+1], path + File.separator + v[3*i+2]);
            }
        }
    }

    /**
     * 递归删除整个指定的文件夹
     *
     * @param file
     */
    private static void deleteFiles(File file){
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

    /**
     * 打印当前分支的commit历史记录
     *
     * @return 返回存放commit历史记录的LinkedList
     * @throws Exception readFileString()可能会抛出FileNotFoundException
     */
    public static LinkedList commitList() throws Exception {
        LinkedList commitList = new LinkedList<String>();
        String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        String newCommitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + branch);
        commitList.addLast(newCommitKey);
        generateCommitList(commitList, newCommitKey);

        for (Object o : commitList) {
            System.out.println(o);
        }
        return commitList;
    }

    /**
     * 往LinkedList里递归添加commit历史记录
     *
     * @param commitList 存放commit历史记录的LinkedList
     * @param newCommitKey 递归下去的commit key
     * @throws Exception readFileString()可能会抛出FileNotFoundException
     */
    private static void generateCommitList(LinkedList commitList, String newCommitKey) throws Exception {
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + newCommitKey);
        String[] v = commitValue.split(" |\n");
        try {
            commitList.add(v[3]);
            generateCommitList(commitList, v[3]);
        } catch (Exception ignored) {}
    }

    /**
     * 版本回退
     *
     * @param commitKey 要回退到到版本号
     * @throws Exception readFileString()可能会抛出FileNotFoundException
     */
    public static void resetVersion(String commitKey) throws Exception {
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + commitKey);
        String[] v = commitValue.split(" |\n");
        String treeKey = v[1];
        final String treePath = new File("Objects").getAbsolutePath();
        deleteFiles(new File("../workspace"));
        new File("../workspace").mkdir();
        rewriteBranchFiles(treePath, treeKey, "../workspace");

        // 删除当前分支对应的commit历史里多余版本的key
        // 当前所在分支的名字
        String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        String path = new File("Branch").getAbsolutePath() + File.separator + branch + "CommitHistory";
        // 当前所在分支的commit历史
        String commitHistory = KeyValueStore.readFileString(path);
        // 获取回退版本的 commit key 所在的位置
        int index = commitHistory.indexOf(commitKey);
        String newCommitHistory = commitHistory.substring(0, index);
        // 把截取好的commit历史重新写进Commit History
        try(FileOutputStream outputStream = new FileOutputStream(path)){
            outputStream.write(newCommitHistory.getBytes());
        }

        // 删除未被引用的Objects











    }
}
