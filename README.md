# mybatis-工具箱
> 包含功能如下

- 1. 控制台输出完整sql语句(即 ?变 真实值)  
- 2. 预计下一版本出现


## 如何使用

> maven 依赖  (正在提交阿里云镜像库中。。。)
```
		<dependency>
			<groupId>org.kd</groupId>
			<artifactId>mybatis-tool-box</artifactId>
			<version>1.0-M1</version>
		</dependency>
```

> 在 mybatis mybatisConfig.xml 添加
```
	<plugins> 
		<!-- 打印完整sql语句   mysad -->
		<plugin interceptor="org.kd.interceptor.MybatisAutoSql" />
	</plugins>  
```

> 测试 -控制台输出
```
========================================================
https://hpit-bat.github.io/hpit-BAT-home/
========================================================
执行XML方法:org.kd.dao.UserDao.selectAll
执行的完整的sql语句-------------------mysad
SELECT id,name,pwd,user_name,age,birthday,add_time,head_img,create_time,update_time,del_flag FROM user ORDER BY create_time desc,update_time desc
执行的sql语句的时间:7ms
```		