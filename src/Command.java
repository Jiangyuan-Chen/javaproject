import java.util.Scanner;

/**
 * @author futongyao
 * @author chenjiangyuan
 */
public class Command {

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("请输入您想进行的操作：");
            Scanner input = new Scanner(System.in);
            String command = input.nextLine();
            String[] v = command.split(" ");

            // 创建分支操作命令为kdg branch branchname
            if (v[0].equals("kdg")&&v[1].equals("branch")) {
                try{
                    new Branch(v[2]).write();
                    System.out.println("分支已成功创建！");
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Input error!");
                }
            }

            // 切换分支操作命令为kdg switch branchName
            else if (v[0].equals("kdg")&&v[1].equals("switch")) {
                if (v.length == 4 && "-c".equals(v[2])){
                    new Branch(v[3]).write();
                    Branch.switchBranch(v[3]);
                    System.out.println("分支成功创建且已切换至分支"+v[3]);
                }
                else if (v.length == 3){
                    Branch.switchBranch(v[2]);
                    System.out.println("分支已切换至" + v[2]);
                }
                else{
                System.out.println("Input error!");
                }
            }

            // commit操作命令为kdg commit
            else if (v[0].equals("kdg")&&v[1].equals("commit")) {
                new Commit().write();
                System.out.println("已完成commit");
            }

            // 查询当前分支commit历史操作命令为kdg log
            else if (v[0].equals("kdg")&&v[1].equals("log")) {
                System.out.println("当前分支commitList为：");
                Branch.commitList();
            }

            // 回滚操作命令为kdg reset resetkey,resetkey为要回滚到的commit的key
            else if (v[0].equals("kdg")&&v[1].equals("reset")) {
                try{
                    Branch.resetVersion(v[2]);
                    System.out.println("已成功回滚！");
                } catch (ArrayIndexOutOfBoundsException e){
                    System.out.println("Input error!");
                }
            }

            // 退出操作命令为kdg quit
            else if (v[0].equals("kdg")&&v[1].equals("quit")) {
                return;
            }

            else {
                System.out.println("Input error!");
            }
        }
    }
}
