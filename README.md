# 分布式id生成-idcenter

1. 简介
> 生成全局唯一的id（流水号），是很多公司都需要解决的问题。如果还是采用时间戳+随机数形式生成，在并发量大时，很有可能会生成重复的id。重复id的危害就是会导致一系列问题，比如幂等性。idcenter专门用来高效的生成全局唯一id，分为服务端和客户端，每个客户端的tps可达到150万，而且服务端毫无压力。

2. 环境要求
> * 服务端：jdk1.8
> * 客户端：jdk1.8
> * MySql


> 注意：本系统已经上传到[maven中央库](http://search.maven.org/#search%7Cga%7C1%7Corg.antframework.idcenter)

3. 技术交流和支持
> 欢迎加我微信（zhong_xun_），进行技术交流和支持。如果本项目对你有帮助，欢迎Star和Fork。

## 1. 整体设计
idcenter分为服务端和客户端，服务端和客户端是通过http请求进行交互。

### 1.1 整体设计图
<img src="https://note.youdao.com/yws/api/personal/file/WEBfc5bbb9a03165af0546003f2f430c422?method=download&shareKey=89ba0f4d979d157a46b5b07507925d80" width=700 />

### 1.2 服务端
服务端存储管理不同业务线需要的id，对id进行统一管理。接收客户端获取id的请求，并返回一批id给客户端。

id分为两部分：id所在的时间周期+id值。id具有周期概念（每个小时（天、月、年）算一个周期或无周期），当时间更替到下个周期时，id值自动重置到0（如果周期类型为“无周期”，则id值不会被重置）。每个id（id所在的时间周期+id值）是唯一的。

下面是主要表的结构：
```
id提供者主要表结构：
+-------------+--------------+------+-----+---------+----------------+
| Field       | Type         | Null | Key | Default | Extra          |
+-------------+--------------+------+-----+---------+----------------+
| id_code     | varchar(128) | YES  | UNI | NULL    |                |
| period_type | varchar(40)  | YES  |     | NULL    |                |
| max_id      | bigint(20)   | YES  |     | NULL    |                |
| max_amount  | int(11)      | YES  |     | NULL    |                |
| factor      | int(11)      | YES  |     | NULL    |                |
+-------------+--------------+------+-----+---------+----------------+
重要字段说明
id_code：id编码，每种类型id的唯一标识。
period_type：周期类型（小时、天、月、年、无）
max_id：id在一个周期内允许的最大值（不包含），null表示不限制。
max_amount：客户端一次请求允许获取的最多id数量（包含），null表示不限制。
factor：该id提供者具有的生产者数量。
```
```
id生产者主要表结构：
+----------------+--------------+------+-----+---------+----------------+
| Field          | Type         | Null | Key | Default | Extra          |
+----------------+--------------+------+-----+---------+----------------+
| id_code        | varchar(128) | YES  | MUL | NULL    |                |
| index          | int(11)      | YES  |     | NULL    |                |
| current_period | datetime     | YES  |     | NULL    |                |
| current_id     | bigint(20)   | YES  |     | NULL    |                |
+----------------+--------------+------+-----+---------+----------------+
重要字段说明
id_code：id编码，标识本生产者属于哪个id提供者
index：生产者的序号，id_code+index标识一个唯一的生产者
current_period：当前周期，标识本生产者当前生产到了哪个周期。如果周期类型为无，则当前周期为null
current_id：当前id，标识本生产者在当前周期下生产到了哪个id
```

### 1.3 客户端
客户端刚启动时会尝试从服务端获取一批id，并缓存起来，供后续使用方获取id。使用方获取id时，客户端会从缓存的id存量中获取一个全新的id返回给使用方。并且客户端会根据最近一段时间内id使用频率，自动评估当前剩余的id存量是否足够客户端运行一段时间；如果id存量不够，则客户端会异步请求服务端获取一批id，并缓缓存起来。

## 2. 服务端部署
[下载服务端](https://repo.maven.apache.org/maven2/org/antframework/idcenter/idcenter-assemble/1.2.0.RELEASE/idcenter-assemble-1.2.0.RELEASE-exec.jar)。以下是集群部署架构图：<br/>

<img src="https://note.youdao.com/yws/api/personal/file/WEBe5a50ed3b7de63c48e1629675dbece01?method=download&shareKey=c1b635c5b90590dc21b9a29ba9f61772" width=600 />

<span style="font-size: large">说明：</span>
- 服务端使用的springboot，直接命令启动下载好的jar包即可，无需部署tomcat。
- 服务端使用hibernate自动生成表结构，无需导入sql（只需要服务端第一次启动时拥有向数据库执行ddl语句权限，启动成功后就可以删除该权限，以后每次启动都不需要该权限）。
- 服务端在启动时会在"/var/apps/"下创建日志文件，请确保服务端对该目录拥有写权限。
- 服务端http端口为6210。

启动服务端命令模板：
```bash
java -jar idcenter-assemble-1.2.0.RELEASE-exec.jar --spring.profiles.active="online" --spring.datasource.url="数据库连接" --spring.datasource.username="数据库用户名" --spring.datasource.password="数据库密码"
```
比如我本地开发时启动命令：
```bash
java -jar idcenter-assemble-1.2.0.RELEASE-exec.jar --spring.profiles.active="online" --spring.datasource.url="jdbc:mysql://localhost:3306/idcenter-dev?useUnicode=true&characterEncoding=utf-8" --spring.datasource.username="root" --spring.datasource.password="root"
```

## 3. 集成客户端
> 读者也可以先看后面的“[id管理介绍](#4-id管理介绍)”，再来看本部分的客户端介绍。

### 3.1 引入客户端依赖
```xml
<dependency>
    <groupId>org.antframework.idcenter</groupId>
    <artifactId>idcenter-client</artifactId>
    <version>1.2.0.RELEASE</version>
</dependency>
```

### 3.2 使用客户端
客户端就是Java类，直接new就可以，只是需要传给它相应参数。一个应用可以创建多个客户端，每个客户端之间互不影响。
```java
// 准备初始化参数
IdContext.InitParams initParams = new IdContext.InitParams();
initParams.setIdCode("common-uid"); // id编码
initParams.setServerUrl("http://localhost:6210");   // 服务端地址
initParams.setInitAmount(1000); // 初始化时获取的id数量
initParams.setMinTime(10 * 60 * 1000);  // 最小预留时间（毫秒，服务端不可用时客户端能够维持的最小时间）
initParams.setMaxTime(15 * 60 * 1000);  // 最大预留时间（毫秒，服务端不可用时客户端能够维持的最大时间）
// 最大预留时间减去最小预留时间的差值就是客户端请求服务端的平均间隔时间，
// 这个差值也是从客户端获取的id的周期误差时间，建议合理设置。比如差值为5分钟应该是适合绝大多数公司的。

IdContext idContext = new IdContext(initParams);    // 创建客户端

// 客户端创建成功后就可以直接获取id
Id id1 = idContext.getAcquirer().getId();
Id id2 = idContext.getAcquirer().getId();
// 以上获取到的是最原始的id形式，使用方可以根据需要将id格式化为自己需要的格式
// 下面得到格式化后的id，比如：2018090700001
String idStr= id1.getPeriod().toString() + String.format("%05d", id1.getId());

// 系统正常运行。。。

// 当系统运行结束时，需关闭客户端释放相关资源
idContext.close();
```

## 4. id管理介绍
进入服务端地址模板：http://IP地址:6210

4.1 第一次进入服务端需初始化一个超级管理员<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB6fc7434f34e217560efa7c7c71b943df?method=download&shareKey=3199c97df78148a74af4bccca23b8718" width=700 />

4.2 点击确定，进入初始化页面<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB97df53e5e8e237b77d9f150e6b202a43?method=download&shareKey=068e3ed25246ec304bedabf1ab13e5b2" width=700 />

4.3 初始化完成后跳转回登录页面，进行登录<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBc3971900764b129441f7fca40b362a9e?method=download&shareKey=191605095cb837286b28b1fc664adb01" width=700 />

4.4 登录成功后，进入id管理页面<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBc63ae1b8d473d1ec05806230bf6cd89f?method=download&shareKey=3fe0c9a9118a142e9a4412d0016e0d24" width=700 />

4.5 点击新增按钮，创建新的id提供者<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB532b3da2806b88a1866eba1c08ecb830?method=download&shareKey=87edce97fdbf0e85131daedb969c5c83" width=700 />

4.6 可以选择周期类型，周期类型表示：每一个小时（天、月、年）算一个周期，或者无周期<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBe38a2cb5eac2f3a743796986e39e1ddc?method=download&shareKey=a048195f3b4d30a74c3be3e78328e232" width=700 />

4.7 点击提交创建id提供者<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB82844f69930cf19d0caf236c590eef71?method=download&shareKey=3652df6e16a9a047e07cffe05c665cbc" width=700 />

4.8 点击上图的修改按钮，可修改id最大值、单次获取id最大数量、生产者数量<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB869cb5c79de1e8554d63db3d01c8b1bf?method=download&shareKey=428a90609087bbd96336e95afcc26a3c" width=700 />

4.9 点击上图✅按钮，提交修改<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB46376e8cd3c2535601f81ca8242714db?method=download&shareKey=7e45972126e31e17ed5643016f3536cd" width=700 />

4.10 点击上图的修改当前数据按钮，进入修改当前周期和当前id页面<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB23dc83c6ea04db3ed77a7f8866d298c4?method=download&shareKey=44c8ee04db4d186b29f6a4e8ddb63a75" width=700 />

4.11 点击提交后，当前周期和当前id就被修改了<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBa0c0b38239ec79b1fe4ef3d38a47cd27?method=download&shareKey=526415ac304a9a875e67a35d93f581a9" width=700 />

4.12 点击左侧导航栏管理员一栏，进行管理员管理。<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB8238d7dee55b6effe5091311b250b90b?method=download&shareKey=515f989d2199690120dbffb57b2c6f3f" width=700 />

4.13 点击上图新增按钮，可创建管理员。管理员分为两种：超级管理员和普通管理员。超级管理员拥有所有权限可以管理所有id提供者，普通管理员只能管理被分配给他的id提供者<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBd164fc0470364eee6455572e3714b586?method=download&shareKey=de8bd0f116f7dfd17014b8395bd05cc5" width=700 />

4.14 点击提交后，管理员就被创建了<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB38a3d8561da4dada06709d9afa356c7a?method=download&shareKey=ad0c81c7b80380047a21a170757d3655" width=700 />

4.15 点击左侧导航栏权限一栏，进行权限管理<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB59ec78764ae2ecd93de80ce0af32b8d0?method=download&shareKey=669280ea78781391d1f609a14c5dd83e" width=700 />

4.16 点击上图新增按钮，可给管理员添加权限<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEB6d7215cbc4a8678a82c36205183a689c?method=download&shareKey=41b4b2ab5a5925938618f1b8cf3f9731" width=700 />

4.17 添加权限后如下<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBdf09cd501e7e8ec01db72593e52799b8?method=download&shareKey=6a820f73ed1f6e22baaf0e60bd440611" width=700 />
