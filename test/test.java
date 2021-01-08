/**
 * @author chenjiangyuan
 */
public class test {
    public static void main(String[] args) throws Exception {
        new Branch("main").write();
        new Commit().write();
        new Branch("test").write();
        new Commit().write();
        Branch.switchBranch("test");
        Branch.commitList();

        System.out.println("成功");
    }


}
