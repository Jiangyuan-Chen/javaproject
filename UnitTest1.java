public class UnitTest1 {
    public static void main(String[] args) throws Exception {

        KeyValueStore kvs = new KeyValueStore();

        // 测试WriteFile()
        kvs.WriteFile("/Users/chenjiangyuan/learnjava/aaa/bbb.txt");
        System.out.println("WriteFile()执行成功");

        // 测试WriteString()
        kvs.WriteString("qwerty");
        System.out.println("WriteString()执行成功");

        // 测试getValue(String key)
        System.out.println(kvs.getValue("bbb.txt"));

        // 测试getValue(String key, String path)
        // System.out.println(kvs.getValue("bbb.txt", "/Users/chenjiangyuan/IdeaProjects/Testing"));
    }
}
