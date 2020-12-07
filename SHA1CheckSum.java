import java.awt.*;
import java.io.*;
import java.security.MessageDigest;


public class SHA1CheckSum {
    private String path;

    SHA1CheckSum(String path){
        this.path = path;
    }

    // 移动文件到指定目录并哈希
    public void WriteFile(String sourceFile){
        try {
            File source = new File(sourceFile);
            String sourceFileName = source.getName(); // 获得文件的名称
            File destination = new File(path + File.separator + sourceFileName); // 文件存放的最终目录
            source.renameTo(destination); // 移动文件到最终目录
            FileInputStream is = new FileInputStream(destination);
            File finality = new File(path + File.separator + SHA1Checksum(is)); // 对在最终目录的文件求哈希值
            destination.renameTo(finality); // 重命名文件名为哈希值
            System.out.println("文件移动成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 在指定目录新建文件，value为输入的字符串，key为文本文档的哈希值
    public void WriteString(String s) {
        try {
            File file = new File(path + File.separator + "a"); // 新建一个名为"a"的空文件
            try (PrintWriter output = new PrintWriter(file)) {
                output.println(s); // 向文件a写入字符串
            }
            FileInputStream is = new FileInputStream(file);
            File finality = new File(path + File.separator + SHA1Checksum(is)); // 对在最终目录的文件求哈希值
            file.renameTo(finality); // 重命名文件为新生成的哈希值
            System.out.println("文本文件创建成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 给定key，获得文件的value
    public void getValue(String hash) {
        dfs(path, hash);
    }

    // 递归查找文件名是否与输入的哈希值匹配，并打开文件
    private void dfs(String path, String hash){
        try {
            File dir = new File(path);
            File[] fs = dir.listFiles(); //将目录里的文件和文件夹对象放入数组fs
            //按顺序遍历每个对象里的文件名是否匹配hash
            for(int i = 0; i < fs.length; i++) {
                if(fs[i].isFile() && fs[i].getName().equals(hash)) {
                    Desktop.getDesktop().open(fs[i]); // 打开匹配的文件
                    System.out.println("文件已打开");
                    return;
                }
                // 对象如果是文件夹则继续递归遍历文件夹里的内容
                if(fs[i].isDirectory()) {
                    dfs(path + File.separator + fs[i].getName(), hash);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 计算文件的哈希值
    private static String SHA1Checksum(InputStream is) throws Exception {
        // 用于计算hash值的文件缓冲区
        byte[] buffer = new byte[1024];
        // 使用SHA1哈希/摘要算法
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        do {
            // 读出numRead字节到buffer中
            numRead = is.read(buffer);
            if (numRead > 0) {
                // 根据buffer[0:numRead]的内容更新hash值
                complete.update(buffer, 0, numRead);
            }
            // 文件已读完，退出循环
        } while (numRead != -1);
        // 关闭输入流
        is.close();
        return HexadecimalHash(complete);
    }

    // 计算字符串的哈希值
    private static String Sha1Checksum(String value) throws Exception{
        MessageDigest complete = MessageDigest.getInstance("SHA-1");
        int numRead = 0;
        complete.update(value.getBytes());
        return HexadecimalHash(complete);
    }

    // 计算SHA1的十六进制的哈希值
    private static String HexadecimalHash(MessageDigest complete){
        byte[] sha1 = complete.digest();
        String result = "";
        for (int i = 0; i < sha1.length; i++) {
            result += Integer.toString(sha1[i]&0xFF, 16);
        }
        return result;
    }
}
