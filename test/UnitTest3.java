import java.io.File;

public class UnitTest3 {
    public static void main(String[] args) throws Exception{
        // 自动生成测试文件夹和测试文件
        File test = new File(System.getProperty("user.dir") + File.separator + "aaa" + File.separator + "bbb" + File.separator + "ccc");
        test.mkdirs(); // 生成测试文件夹
        /*
         * 获取三级文件夹的目录
         * 最后一个File对象是为了检验能否不生成相同的commit
         */
        File[] filedir = {test.getParentFile().getParentFile(), test.getParentFile(), test, test};
        for (File f : filedir) {
            String newCommit = "";
            Commit c = new Commit();
            // 分割commit的value
            String[] v = c.getValue().split(" |\n");
            // 获取当前commit的根目录名字
            String treeName = v[1];
            try { // 最新commit的key
                newCommit = v[3];
                // 比较最新commit的key和HEAD是否相同
                if (!newCommit.equals(KeyValueStore.readFileString("HEAD"))){
                    System.out.println("commit的HEAD与HEAD里的内容不相同，commit生成错误");
                    return;
                }
            }
            /*
             * 当第一次commit时newCommit不存在，v[3]会数组越界。
             * 此时忽略比较最新commit的key和HEAD继续执行程序
            */
            catch (ArrayIndexOutOfBoundsException ignored){}
            // 比较commit的根目录和用户commit的根目录是否相同
            if (!f.getName().equals(treeName)){
                System.out.println("commit的树不相同，commit生成错误");
                return;
            }
            c.write();
            /*
             * HEAD更新后比较HEAD里commit的key和最新commit的key是否相同
             * 由于当提交相同的tree导致生成commit失败时也会进入下述if语句
             * 因此需要同时确认上次commit的的key和新更新的HEAD是否还是相同的
             */
            if (!c.getName().equals(KeyValueStore.readFileString("HEAD")) && !newCommit.equals(KeyValueStore.readFileString("HEAD"))){
                System.out.println("HEAD不是最新的commit的name，commit生成错误");
                return;
            }
        }
        System.out.println("commit已成功正确生成");
    }
}