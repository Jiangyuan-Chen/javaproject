
import java.io.*;
import java.util.Scanner;

public class Command {

    public static void main(String[] args) throws Exception {
        while (true) {
            System.out.print("请输入您想进行的操作：");
            Scanner input = new Scanner(System.in);
            String command = input.nextLine();
            String[] v = command.split(" ");

            // 创建分支操作命令为kdg branch branchname
            if (v[0].equals("kdg")&&v[1].equals("branch")) {
                    new Branch(v[2]).write();
                    System.out.println("分支已成功创建！");
                }

            // 切换分支操作命令为kdg checkout branchname
            else if (v[0].equals("kdg")&&v[1].equals("switch")) {
                    Branch.switchBranch(v[2]);
                    System.out.println("分支已切换至" + v[2]);
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
                Branch.resetVersion(v[2]);
                System.out.println("已成功回滚！");
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
