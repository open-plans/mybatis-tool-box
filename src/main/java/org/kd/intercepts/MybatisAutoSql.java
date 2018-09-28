/**
 * 代号:隐无为 2017：厚溥
 * 文件名：QueryInterceptor.java
 * 日期：2018年1月4日
 * 修改人：
 * 描述：
 */
package org.kd.intercepts;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;

/**
 * 用途：MyBatis 性能拦截器，用于输出每条 SQL 语句及其执行时间  //目前用第二版本    可以看看第三版本
 * 说明
 * https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/Interceptor.md
 */
@Intercepts(
	    {
	        @Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class }),
	        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
	        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
	    }
	)
	public class MybatisAutoSql implements Interceptor {

	    @Override
	    public Object intercept(Invocation invocation) throws Throwable {
	        Object[] args = invocation.getArgs();
	        MappedStatement ms = (MappedStatement) args[0];
	        Object parameter = args[1];
	        BoundSql boundSql = null;
	        String sqlId = ms.getId();
	        Configuration configuration = ms.getConfiguration();
	        // 查询
	        if(args.length >2) {
	        RowBounds rowBounds = (RowBounds) args[2];
	        //ResultHandler resultHandler = (ResultHandler) args[3];
	        Executor executor = (Executor) invocation.getTarget();
	        CacheKey cacheKey;
	        //由于逻辑关系，只会进入一次
	        if(args.length == 4){
	            //4 个参数时
	            boundSql = ms.getBoundSql(parameter);
	            cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
	        } else {
	            //6 个参数时
	            cacheKey = (CacheKey) args[4];
	            boundSql = (BoundSql) args[5];
	        }
	        }else {
	             boundSql = ms.getBoundSql(parameter);
	        }
	        long start = System.currentTimeMillis();
	        String sql = getSql(configuration, boundSql, sqlId);
	        System.err.println("========================================================");
	        System.err.println("https://hpit-bat.github.io/hpit-BAT-home"+"[隐无为]");
	        System.err.println("========================================================");
	        System.err.println("执行XML方法:"+sqlId);
	        System.err.println("执行的完整的sql语句-------------------mysad");
	        System.err.println(sql);
	        // 执行
	        Object  proceed=invocation.proceed();
	        //TODO 自己要进行的各种处理
	        //注：下面的方法可以根据自己的逻辑调用多次，在分页插件中，count 和 page 各调用了一次
	        //executor.query(ms, parameter, rowBounds, resultHandler, cacheKey, boundSql);
	        long end = System.currentTimeMillis();
	        long time = (end - start);
	        if (time > 1) {
	            System.err.println("执行的sql语句的时间:"+time+"ms");
	        }
	        return proceed;
	    }

	    public static String getSql(Configuration configuration, BoundSql boundSql, String sqlId) {
	        String sql = showSql(configuration, boundSql);
	        StringBuilder str = new StringBuilder(100);
	        str.append(sql);
	        return str.toString();
	    }
	 
	    private static String getParameterValue(Object obj) {
	        String value = null;
	        if (obj instanceof String) {
	            value = "'" + obj.toString() + "'";
	        } else if (obj instanceof Date) {
	            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
	            value = "'" + formatter.format(obj) + "'";
	        } else {
	            if (obj != null) {
	                value = obj.toString();
	            } else {
	                value = "null";
	            }
	 
	        }
	        return value;
	    }
	 
	    public static String showSql(Configuration configuration, BoundSql boundSql) {
	        Object parameterObject = boundSql.getParameterObject();
	        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
	        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
	        if (parameterMappings.size() > 0 && parameterObject != null) {
	            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
	            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
	                sql = sql.replaceFirst("\\?", getParameterValue(parameterObject));
	 
	            } else {
	                MetaObject metaObject = configuration.newMetaObject(parameterObject);
	                for (ParameterMapping parameterMapping : parameterMappings) {
	                    String propertyName = parameterMapping.getProperty();
	                    if (metaObject.hasGetter(propertyName)) {
	                        Object obj = metaObject.getValue(propertyName);
	                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
	                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
	                        Object obj = boundSql.getAdditionalParameter(propertyName);
	                        sql = sql.replaceFirst("\\?", getParameterValue(obj));
	                    }
	                }
	            }
	        }
	        return sql;
	    }
	    
	    @Override
	    public Object plugin(Object target) {
	        return Plugin.wrap(target, this);
	    }

	    @Override
	    public void setProperties(Properties properties) {
	    }

	}