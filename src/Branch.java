import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author chenjiangyuan
 */
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
                System.out.println("分支已存在！");
                return;
            }
        }catch (FileNotFoundException ignored){}

        // 写出文件Branch
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
                    outputStream.write((branchCommitKey + "/n").getBytes());
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
        String commitValue = "";
        // 如果没有在被切换的分支上提交commit的话，branchCommitKey为None，无法找到Object里的commit，不能读出commitValue
        try{
            commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + branchCommitKey);
            String[] v = commitValue.split(" |\n");
            // 获得切换后分支最新的commit里根目录的key
            String treeKey = v[1];
            final String treePath = new File("Objects").getAbsolutePath();
            // 删除工作目录后再重新创建
            deleteFiles(new File("../workspace"));
            new File("../workspace").mkdir();
            // 把切换后分支的文件内容递归写进工作区
            rewriteBranchFiles(treePath, treeKey, "../workspace");
        } catch (FileNotFoundException ignored){}
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
        // 根目录的tree key
        String treeValue = KeyValueStore.readFileString(treePath + File.separator + treeKey);
        String[] v = treeValue.split(" |\n|\t");
        //判断key对应的Object类型并写出文件
        for (int i = 0; i < v.length/3; i++) {
            // 如果是Blob直接写出
            if ("Blob".equals(v[3*i])) {
                File blob = new File(new File("Objects").getAbsolutePath() + File.separator + v[3*i+1]);
                new KeyValueStore(v[3*i+2], blob).writeFile(new File(path));
            }
            // 如果是Tree则先写出文件夹再递归进入这个文件夹写文件夹或文件
            if ("Tree".equals(v[3*i])) {
                new File(path, v[3*i+2]).mkdir();
                rewriteBranchFiles(treePath, v[3*i+1], path + File.separator + v[3*i+2]);
            }
        }
    }

    /**
     * 递归删除整个指定的文件夹
     *
     * @param file 要删除的文件夹File
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
        // 获取当前所在分支的名字
        String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        // 获取当前所在分支最新的commit key
        String newCommitKey = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + branch);
        // 把当前所在分支最新的commit key添加到commitlist
        commitList.addLast(newCommitKey);
        // 往LinkedList里递归添加当前分支commit的历史记录
        generateCommitList(commitList, newCommitKey);
        // 打印commit的历史记录
        for (Object o : commitList) {
            System.out.println(o);
        }
        return commitList;
    }

    /**
     * 往LinkedList里递归添加当前分支commit的历史记录
     *
     * @param commitList 存放commit历史记录的LinkedList
     * @param newCommitKey 递归下去的commit key
     * @throws Exception readFileString()可能会抛出FileNotFoundException；当到最开始的commit时v[3]越界会抛出ArrayIndexOutOfBoundsException
     */
    private static void generateCommitList(LinkedList commitList, String newCommitKey) throws Exception {
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + newCommitKey);
        String[] v = commitValue.split(" |\n");
        // 递归到v[3]越界时说明已经递归到本分支第一次commit，结束递归
        try {
            // v[3]为本次commit的parent's commit key
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
        // 获取根目录的tree key
        String commitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + commitKey);
        String[] v = commitValue.split(" |\n");
        String treeKey = v[1];

        // 把被回滚commit里的文件内容写回工作区
        final String treePath = new File("Objects").getAbsolutePath();
        // 清空工作区
        deleteFiles(new File("../workspace"));
        new File("../workspace").mkdir();
        rewriteBranchFiles(treePath, treeKey, "../workspace");

        // 删除当前分支对应的commit历史里多余版本的key
        // 当前所在分支的名字
        String branch = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "HEAD");
        String path = new File("Branch").getAbsolutePath() + File.separator + branch + "CommitHistory";
        // 当前所在分支的commit历史
        String commitHistory = KeyValueStore.readFileString(path);
        // 获取回退版本的commit key所在的位置
        int index = commitHistory.indexOf(commitKey);
        String newCommitHistory = commitHistory.substring(0, index + commitKey.length());
        // 把截取好的commit历史重新写进Commit History
        try(FileOutputStream outputStream = new FileOutputStream(path)){
            outputStream.write((newCommitHistory + "\n").getBytes());
        }

        // 删除未被引用的Objects
        // 获取所有分支的名字
        String[] branchList = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + "Branch List.txt").split("\n");
        String tempCommit = "";
        // 获取所有分支commit历史记录的commit key
        for (int i = 0; i < branchList.length; i++){
            String tempCommitHistory = KeyValueStore.readFileString(new File("Branch").getAbsolutePath() + File.separator + branchList[i] + "CommitHistory");
            tempCommit += tempCommitHistory;
        }
        // 新建一个ArrayList存放这些commit key所对应的Object Name
        ArrayList<String> tempObjectName = new ArrayList<>();
        // 把所有commit key分开放入一个数组中
        String[] tempCommitList = tempCommit.split("\n");
        // 找到每个commit key对应的Objects并把它们的名字写入tempObjectName
        for (int i = 0; i < tempCommitList.length; i++){
            // 被回滚的tempCommit里的value
            String tempCommitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + tempCommitList[i]);
            String[] tempValue = tempCommitValue.split(" |\n");
            // 获得被回滚的tempCommit里根目录的key
            String tempTreeKey = tempValue[1];
            findCommitFiles(treePath, tempTreeKey, tempObjectName);
        }

        // 被删除的Commit History
        String oldCommitHistory = commitHistory.substring(index + 40 + 1);
        // 把所有oldCommitHistory里的commit key分开放入一个数组中
        String[] oldCommitList = oldCommitHistory.split("\n");
        // 删除每个commit object和每个commit key所对应的所有blob objects和tree objects
        for (int j = 0; j < oldCommitList.length; j++){
            if (!tempCommit.contains(oldCommitList[j])) {
                // 被回滚的oldCommit里的value
                String oldCommitValue = KeyValueStore.readFileString(new File("Objects").getAbsolutePath() + File.separator + oldCommitList[j]);
                String[] value = oldCommitValue.split(" |\n");
                // 获得被回滚的oldCommit里根目录的key
                String oldTreeKey = value[1];
                File commit = new File(new File("Objects").getAbsolutePath() + File.separator + oldCommitList[j]);
                // 删除commit object
                commit.delete();
                // 递归删除当前commit里根目录所对应的所有objects
                deleteCommitFiles(treePath, oldTreeKey, tempObjectName);
            }
        }
        // 更新文件夹Branch里当前分支的指针，令其指向本次最新的commit key
        try (FileOutputStream outputStream = new FileOutputStream(new File("Branch").getAbsolutePath() + File.separator + branch))
        { outputStream.write(commitKey.getBytes()); }
    }

    /**
     * 递归删除指定commit里根目录所对应的所有objects
     *
     * @param treePath 文件夹Object的绝对路径
     * @param oldTreeKey 未被引用的commit所对应的根目录的tree key
     * @param tempObjectName 存放这些commit key所对应的Object Name的容器ArrayList
     * @throws Exception 方法readFileString可能会抛出FileNotFoundException
     */
    private static void deleteCommitFiles(String treePath, String oldTreeKey, ArrayList<String> tempObjectName) throws Exception {
        try{
            String treeValue = KeyValueStore.readFileString(treePath + File.separator + oldTreeKey);
            // 分割tree value
            String[] vtree = treeValue.split(" |\n|\t");
            File t = new File(new File("Objects").getAbsolutePath() + File.separator + oldTreeKey);
            // 删除commit object
            t.delete();
            // 判断key对应的Object类型并删除
            for (int i = 0; i < vtree.length/3; i++) {
                // 如果是Blob且这个Blob未被引用，则直接删除
                if (!tempObjectName.contains(vtree[3*i+1]) && "Blob".equals(vtree[3*i])) {
                    File blob = new File(new File("Objects").getAbsolutePath() + File.separator + vtree[3*i+1]);
                    blob.delete();
                }
                // 如果是Tree且这个Tree未被引用则先删除这个tree再递归进这个tree key
                if (!tempObjectName.contains(vtree[3*i+1]) && "Tree".equals(vtree[3*i])) {
                    deleteCommitFiles(treePath, vtree[3*i+1], tempObjectName);
                }
            }
        }catch (FileNotFoundException ignored){}
    }

    /**
     * 找到每个commit key对应的Objects并把它们的名字写入tempObjectName
     *
     * @param treePath 文件夹Object的绝对路径
     * @param tempTreeKey 被引用的commit所对应的根目录的tree key
     * @param tempObjectName 存放这些commit key所对应的Object Name的容器ArrayList
     * @throws Exception 方法readFileString可能会抛出FileNotFoundException
     */
    private static void findCommitFiles(String treePath, String tempTreeKey, ArrayList<String> tempObjectName) throws Exception {
        try{
            String treeValue = KeyValueStore.readFileString(treePath + File.separator + tempTreeKey);
            // 分割tree value
            String[] vtree = treeValue.split(" |\n|\t");
            // 判断key对应的Object类型并添加名字到tempObjectName
            for (int i = 0; i < vtree.length/3; i++) {
                // 如果是Blob直接添加它的名字到tempObjectName
                if ("Blob".equals(vtree[3*i])) {
                    tempObjectName.add(vtree[3*i+1]);
                    continue;
                }
                // 如果是Tree则添加名字到tempObjectName，再递归进这个tree key
                if ("Tree".equals(vtree[3*i])) {
                    tempObjectName.add(vtree[3*i+1]);
                    findCommitFiles(treePath, vtree[3*i+1], tempObjectName);
                }
            }
        }catch (FileNotFoundException ignored){}
    }
}
