## Java第十四小组课程项目设计文档

- #### 实现的交互
  
  - kdg branch [branchname]：新建分支
  
  - kdg switch [branchname]：切换分支
  
  - kdg switch -c [branchname]：新建并切换分支
  
  - kdg commit：commit
  
  - kdg log：查询commit历史
  
  - kdg reset [resetkey]：回滚
  
  - kdg quit：结束操作
  
    
  
  
  
- #### GitObject抽象类（为Tree、Blob、Commit、Branch类的父类）
  
  - 数据域：name、value、file
  - 方法：setName、setValue、setFile、getName、getValue、getFile、write



- #### KeyValueStore类
     
     - ##### 构造方法（共四个，参数中有name的为以key-value形式存储的文件的文件名，即key）：
       
       - KeyValueStore(File file)
       - KeyValueStore(String name, File file)
    - KeyValueStore(String value)
    - KeyValueStore(String name, String value)
    
  - ##### writeFile()方法：
    
    - 在工作目录新建文件，提取源文件的内容到新文件
    
  - ##### writeFile(File path)方法：
    
    - 在指定目录新建文件，提取源文件的内容到新文件
    
  - ##### writeString()方法：
    
    - 在工作目录新建文件，value为文件的字符串，name为文件的名字
    
  - ##### writeString(File path)方法：
    
    - 在指定目录新建文件，value为文件的字符串，name为文件的名字
    
  - ##### writeBranch()方法：
    
    - 在文件夹Branch新建文件，文件名为分支名，内容为最新的commit key
    
  - ##### readFileString(String filename)方法
  
    - 根据文件名自动查找到工作目录下的文件，并读取文件中的字符串
    - 返回被读取的文件内的字符串
  
  - ##### getFileValue(String key)方法：
  
    - 给定key，在指定目录获得文件的value
    - key 为被查找value的文件的key（文件名）
    - 返回被查找value的文件的value（文件内容）



- #### Blob类（继承GitObject类）
  
  - ##### 构造方法：
    
    - 获取指定目录文件并生成文件名（哈希值）
  - ##### write()方法：
    
    - 在工作目录新建文件，提取源文件的内容到新文件，新文件名为哈希值
  - ##### write(File path)方法：
    
    - 在指定目录新建文件，提取源文件的内容到新文件，新文件名为哈希值
  - ##### readFileString(String filename)方法：
    
    - 根据文件名自动查找到工作目录下的文件，并读取文件中的字符串



- #### Tree类（继承GitObject类）
  
  - ##### 构造方法：
    
    - 给定一个文件夹目录，深度优先遍历该文件夹
    - 更新value内容，根据value生成key
  - ##### write()方法：
    
    - 在工作目录新建文件，提取源文件的内容到新文件，新文件名为哈希值
  - ##### write(File path)方法：
    
    - 在指定目录新建文件，提取源文件的内容到新文件，新文件名为哈希值
  - ##### writeTreeFiles(String path)方法：
    
    - 把文件转化为Blob和Tree存入文件夹Objects



- #### Commit类（继承GitObject类）
  
  - ##### 构造方法：
       
       - 对workpace里的内容进行commit
  - ##### write()方法：
       
       - 在文件夹Objects里存储本次commit的信息文件
       - 在文件夹Objects里存储本次commit工作区的tree
       - 更新工作目录里HEAD的指针，令其存储本次最新的commit key
       - 更新文件夹Branch里当前分支的指针，令其存储本次最新的commit key
       - 把工作区的文件转化为Blob和Tree存入文件夹Objects
       - 把本次commit的key写入当前分支的CommitHistory里
  - ##### writeHEAD方法：
    
    - 生成HEAD文件，内容为当前最新commit的key



- #### Branch类

  - ##### 构造方法：

    - 参数为name，创建一个名为name的分支
    - 读取指向最新commit的HEAD的内容（即最新commit的哈希值），将此作为最新分支的内容；若找不到HEAD文件，则现将当前分支内容设置为None
    - 若是第一次操作，在工作目录新建一个Branch文件夹，新建一个Objects文件夹，并在上一级目录新建一个workspace文件夹

  - ##### write()方法：

    - 写一个名为name的最新分支，内容为最新commit的哈希值

    - 将最新分支名写入Branch List.txt中，且保留之前的所有分支名

    - 将最新的分支名写入HEAD中，不保留先前分支名

   - ##### switchBranch(String Branch)方法：

     - 切换分支

     - Branch 为要切换到的分支名

   - ##### rewriteBranchFiles(String treePath, String treeKey, String path)方法：

     - 被switchBranch方法调用
     - 将切换后分支的文件内容写入工作区
     - treePath为文件夹Object的绝对路径
     - treeKey为切换后分支最新的commit里根目录的key开始往下递归
     - path 写文件的目录从工作区目录开始往下递归

   - ##### commitList()方法：

     - 打印当前分支的历史commit记录
     - 返回存放commit历史记录的LinkedList

  - ##### deleteFiles(File file)方法：

    - 递归删除整个指定的文件夹
    - file为要删除的文件夹File

  - ##### generateCommitList(LinkedList commitList, String newCommitKey)方法：

    - 往LinkedList里递归添加commit历史记录
    - commitList 存放commit历史记录的LinkedList
    - newCommitKey 递归下去的commit key

  - ##### resetVersion(String commitKey)方法：

    - 版本回退
    - commitKey 为要回退到到版本号

  - ##### deleteCommitFiles(String treePath, String oldTreeKey, ArrayList<String> tempObjectName)方法：

    - 递归删除指定commit里根目录所对应的所有objects
    - treePath 文件夹Object的绝对路径
    - oldTreeKey 未被引用的commit所对应的根目录的tree key
    - tempObjectName 存放这些commit key所对应的Object Name的容器ArrayList

  - ##### findCommitFiles(String treePath, String tempTreeKey, ArrayList<String> tempObjectName)方法：

    - 找到每个commit key对应的Objects并把它们的名字写入tempObjectName
    - treePath 文件夹Object的绝对路径
    - tempTreeKey 被引用的commit所对应的根目录的tree key
    - tempObjectName 存放这些commit key所对应的Object Name的容器ArrayList



- #### Command类

  - ##### 操作指令：

    - kdg branch [branchname]：新建分支到[branchname]
    - kdg switch [branchname]：切换分支到[branchname]
    - kdg switch -c [branchname]：新建并切换到分支[branchname]
    - kdg commit：对当前workspace内容进行commit
    - kdg log：查询当前分支commit历史
    - kdg reset [resetkey]：回滚，resetkey为要回滚到的commit key，实现了删除workspace里的实体文件以及Objects里对应的keyvalue形式存储的文件
    - kdg quit：结束操作



