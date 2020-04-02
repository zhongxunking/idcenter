# idcenter
1. 简介
> 生成全局唯一的id（流水号），是很多公司都需要解决的问题。idcenter专门用来高效的生成全局唯一id，分为服务端和客户端。一个服务端可以支持海量的客户端请求，同时每个客户端获取id的tps可达到150万。

2. 环境要求
> * 服务端：jdk1.8
> * 客户端：jdk1.8
> * MySQL

> 注意：本系统已经上传到[maven中央库](http://search.maven.org/#search%7Cga%7C1%7Corg.antframework.idcenter)

3. 演示环境
> 地址：http://idcenter.antframework.org:6210 <br/>
> 账号：admin 密码：123
<img src="https://note.youdao.com/yws/api/personal/file/WEB05da7336237569414648a5e625d2302b?method=download&shareKey=5fabc26cd1af6f5013f50dbe918c78b8" width=700 />

# 特性
idcenter具备统一的id管理能力、id支持周期概念、可承受海量获取id需求、完善的权限管理能力。
* 统一的id管理能力：提供id管理页面，可管理公司内部所有的id。
* id支持周期概念：id的周期概念保证了id的时效性，让你看到id就知道这个id是什么时候产生的。
* 服务端支持海量的客户端请求：服务端的预处理设计，保证了服务端可以支持海量的客户端请求。
* 每个客户端可承受海量的id获取请求：客户端的预处理设计，保证了单个客户端可承受百万tps级别的id获取。
* 完善的权限管理能力：可对管理员的权限进行约束，让合适的人管理合适的id。

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
欢迎加我微信（zhong_xun_）入群交流。<br/>
<img src="https://note.youdao.com/yws/api/personal/file/WEBbca9e0a9a6e1ea2d9ab9def1cc90f839?method=download&shareKey=00e90849ae0d3b5cb8ed7dd12bc6842e" width=200 />

# Who is using
欢迎使用idcenter的组织在[这里](https://github.com/zhongxunking/idcenter/issues/1)进行登记（仅供其他用户参考）。