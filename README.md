# idcenter
1. 简介
> 生成全局唯一的id（流水号），是很多公司都需要解决的问题。如果还是采用时间戳+随机数形式生成，在并发量大时，很有可能会生成重复的id。重复id的危害就是可能会导致一系列问题。idcenter专门用来高效的生成全局唯一id，分为服务端和客户端，每个客户端的tps可达到150万，而且服务端毫无压力。

2. 环境要求
> * 服务端：jdk1.8
> * 客户端：jdk1.8
> * MySQL

> 注意：本系统已经上传到[maven中央库](http://search.maven.org/#search%7Cga%7C1%7Corg.antframework.idcenter)

3. 演示环境
> 地址：http://idcenter.antframework.org:6210 <br/>
> 账号：admin 密码：123

# 文档
* 设计<br/>
&ensp;&ensp;[整体设计](https://github.com/zhongxunking/idcenter/wiki/%E6%95%B4%E4%BD%93%E8%AE%BE%E8%AE%A1)
* 部署<br/>
&ensp;&ensp;[部署服务端](https://github.com/zhongxunking/idcenter/wiki/%E9%83%A8%E7%BD%B2%E6%9C%8D%E5%8A%A1%E7%AB%AF)
* 使用<br/>
&ensp;&ensp;[管理Id](https://github.com/zhongxunking/idcenter/wiki/%E7%AE%A1%E7%90%86Id)
* 开发<br/>
&ensp;&ensp;[集成Java客户端](https://github.com/zhongxunking/idcenter/wiki/%E9%9B%86%E6%88%90Java%E5%AE%A2%E6%88%B7%E7%AB%AF)<br/>
&ensp;&ensp;[服务端OpenAPI](https://github.com/zhongxunking/idcenter/wiki/%E6%9C%8D%E5%8A%A1%E7%AB%AFOpenAPI)

# 技术支持
欢迎加我微信入群进行交流。<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBbca9e0a9a6e1ea2d9ab9def1cc90f839?method=download&shareKey=00e90849ae0d3b5cb8ed7dd12bc6842e" width=200 />

# Who is using
欢迎使用idcenter的组织在[这里](https://github.com/zhongxunking/idcenter/issues/1)进行登记（仅供其他用户参考）。