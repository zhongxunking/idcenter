# 分布式id生成-idcenter

1. 简介
> 生成全局唯一的id（流水号），是很多公司都需要解决的问题。如果还是采用时间戳+随机数形式生成，在并发量大时，很有可能会生成重复的id。重复id的危害就是会导致一系列问题，比如幂等性。idcenter专门用来高效的生成全局唯一id，分为服务端和客户端，每个客户端的tps可达到150万，而且服务端无压力。

2. 环境要求
> * 服务端：jdk1.8
> * 客户端：jdk1.8


> 注意：id中心已经上传到[maven中央库](http://search.maven.org/#search%7Cga%7C1%7Corg.antframework.idcenter)

3. 感谢
> 感谢[美团的Leaf系统](https://tech.meituan.com/MT_Leaf.html)设计思路介绍，里面介绍了几种全局id生成技术，建议读者先阅读它。idcenter借鉴了leaf的一些设计思路，同时也进行了优化。

## 1. 整体设计
idcenter分为服务端和客户端。服务端和客户端是通过http请求进行交互。

### 1. 服务端
服务端存储管理各类id的提供者，id提供者具有周期概念（每个小时（天、月、年）算一个周期或无周期），当时间更替到下个周期时，id自动重置到0（如果周期类型为“无”，则id不会被重置）。每个id提供者可以设置多个生产者，每个生产者独立的生产id，这样可成倍的提高服务端效率。当服务端接收到客户端请求时，id提供者会随机选择一个生产者，生产一批id给客户端。

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
id_code：id编码，每种id的唯一标识。
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
### 2. 客户端
使用方通过客户端获取id。客户端会从客户端内的id余量中获取一个id返回给使用方，并且根据最近一段时间内id使用频率，评估当前id余量是否足够客户端运行一段时间。如果id余量不够，则客户端异步请求服务端获取一批id放入id余量中。客户端在id余量不够时最多有一个线程异步请求服务端，所以基本上客户端没有资源损耗。
### 3. 整体设计图
![](https://note.youdao.com/yws/api/personal/file/WEB21517303ba4dba3ce73b76e4c338e1b8?method=download&shareKey=77b353a4a710cef174402c89d6586da3)

## 2. 服务端部署
### 1. [下载服务端](https://repo.maven.apache.org/maven2/org/antframework/idcenter/idcenter-assemble/1.1.0.RELEASE/idcenter-assemble-1.1.0.RELEASE-exec.jar)。说明：
1. 服务端使用的springboot，直接命令启动下载好的jar包即可，无需部署tomcat。
2. 服务端使用hibernate自动生成表结构，无需导入sql。
3. 服务端在启动时会在"/var/apps/"下创建日志文件，请确保服务端对该目录拥有写权限。

### 2. 启动服务端：
启动命令模板：
```
java -jar idcenter-assemble-1.1.0.RELEASE-exec.jar --spring.profiles.active="环境编码" --spring.datasource.url="数据库连接" --spring.datasource.username="数据库用户名" --spring.datasource.password="数据库密码"
```
比如：
```
java -jar idcenter-assemble-1.1.0.RELEASE-exec.jar --spring.profiles.active="online" --spring.datasource.url="jdbc:mysql://localhost:3306/idcenter-dev?useUnicode=true&characterEncoding=utf-8" --spring.datasource.username="root" --spring.datasource.password="root"
```
### 3. 后台管理
后台中管理员有两种：超级管理员、普通管理员。超级管理员可以管理所有id提供者，也可以管理其他管理员；普通管理员只能管理分配给他的id提供者。

登录链接模板：http://IP地址:6210/html/login.html （比如我本地开发时的登录链接：http://localhost:6210/html/login.html ）
#### 1. 第一次使用时会让你设置一个超级管理员：
<img width="600" height="400" src="https://note.youdao.com/yws/api/personal/file/WEB10c31f64f12088d4b5603ba5bf6b6d83?method=download&shareKey=7ebdea09354d98897dfbb4635622ae43"></img>
#### 2. 然后进行登录进入管理页面：
![](https://note.youdao.com/yws/api/personal/file/WEB7fd296e1ce54adcc48d4d337745aa92d?method=download&shareKey=c45a09631757854961a196291bdab140)

## 3. 集成客户端
> 客户端进行了并发控制，并发情况下不会出现问题。客户端不管是单线程还是多线程tps都可以达到150万以上。

### 1. 引入客户端依赖
```
<dependency>
    <groupId>org.antframework.idcenter</groupId>
    <artifactId>idcenter-client</artifactId>
    <version>1.1.0.RELEASE</version>
</dependency>
```
### 2. 使用客户端
客户端就是Java类，直接new就可以，只是需要传给它相应参数。一个应用可以创建多个客户端，每个客户端之间不受影响。
```
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

// 当要关闭系统时，调用下面方法关闭客户端
idContext.close();
```
