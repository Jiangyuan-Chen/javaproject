**第一周任务**

 

**Task1**  实现key-value存储

- 最简单的key-value存储方式

- - key作为文件名，文件内容作为value

- 支持以下功能

- - 给定value，向存储中添加对应的key-value
  - 给定key，查找得到对应的value值

- 封装成class对外提供接口

- 单元测试

 

**Task2**  将一个文件夹转化为key，value

- 给定一个文件夹目录，将其转化成若干tree和blob

深度优先遍历此目录

- - 遇到自文件就转化为blob并保存
  - 遇到子文件夹就递归调用文件夹内部的子文件/文件夹最后构造tree并保存

- 使用task1提供的接口

- 单元测试

 

 

**第二周任务**

 

**Task1**  完善、优化已有的代码

- 完善注释、设计文档和单元测试（使用代码来自动化实现测试用例的生成以及验证测试结果是否正确）

 

**Task2**  实现Commit

- 给定一个工作区目录，生成对应的blob和tree (上周已完成)以及commit
- 写代码之前先理清思路，写设计文档
- 提示：
- - 需要存储指向当前最新commit的HEAD指针
  - 每次新生成一个commit前，需要把根目录的tree key与已有的最新commit的tree key进行比较，发现不相同时（即文件发生了变动）才添加这个commit
- Commit：提交
- - 包含根目录tree对象的key
  - 包含前一次commit的key
  - 以上两行构成本次commit的value，本次commit的key就是以上两行内容的哈希
  - 第一次commit没有前一次commit

 

各个类内容与功能说明：

Class Commit与class Blob和class Tree同样继承于abstract class GitObject，其均实现key-value存储。

 

•     class KeyValueStore 

\1.    writeFile()和writeString()方法：

对于文件和字符串的key-value存储，分别调用writeFile()和writeString()方法；在工作目录新建文件，提取原文件的内容到新文件（先	利用buffer读入文件然后写进新文件）；或是在工作目录新建文件，存储字符串，并以哈希值作为文件名；

\2.    readFileString()方法：

读取文件中的字符串，并返回之；

\3.    getvalue()方法：

给定key，在指定的目录下遍历匹配该值，并得到对应的value值

 

•     abstract class GitObject 

\1.    数据域：name、value、file

\2.    方法：setName()、setValue()、setFile()、getName()、getValue()、getFile()、write()

 

•     class Blob（extends GitObject）

\1.    构造方法：

获取指定目录文件并生成文件名（哈希值）

\2.    重写父类write()方法：

生成文件到工作目录并哈希

 

•     class Tree（extends GitObject）

\1.    构造方法：

给定一个文件存储夹目录，深度优先遍历该文件夹；

更新value内容，根据value生成key。

\2.    重写父类write()方法：

在工作目录生成文件并求哈希值。

 

•     class Commit（extends GitObject）

\1.    构造方法：

获取tree的key，前一次commit的key并输出；

上一条即本次commit的value，对其进行哈希即此次commit的key。

\2.    重写父类write()方法：

生成文件到工作目录；

新生成一个commit前，需要把根目录的tree key与已有的最新commit的tree key进行比较，发现不相同时（即文件发生了变动）才添	加这个commit。

\3.    writeHEAD()方法：

生成HEAD文件，内容为当前最新commit的哈希值。

------