package com.abin.lee.druid.pool.plugins;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Properties;

@Intercepts({ @org.apache.ibatis.plugin.Signature(type = Executor.class, method = "update", args = {
        MappedStatement.class, Object.class }) })
public class OptimisticLockInterceptor implements Interceptor {
    private org.slf4j.Logger log = LoggerFactory.getLogger(OptimisticLockInterceptor.class);

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        if ((invocation.getArgs() != null) && (invocation.getArgs().length > 0) && (invocation.getArgs()[0] != null)
                && ((invocation.getArgs()[0] instanceof MappedStatement))) {

            MappedStatement ms = (MappedStatement) invocation.getArgs()[0];
            Object obj = invocation.getArgs()[1];// args
            if (ms.getSqlCommandType() != SqlCommandType.UPDATE) {
                log.debug("非更新语句,不需要乐观锁!");
            } else { // update
                if (obj == null) {
                    return invocation.proceed();
                }
                if (obj != null && obj.getClass().isAnnotationPresent(VersionIgnored.class)) { // 如果标明不需要乐观锁
                    return invocation.proceed();
                }
                Field field = getField(obj, "version");
                if (field == null) {
//                    return invocation.proceed();
                    throw new RuntimeException("乐观锁的控制的参数必须包含version字段,且是普通PO对象");
                }
                int version = (Integer) getFieldValue(obj, "version");
                SqlSource sqlSource = ms.getSqlSource();
                BoundSql boundsql = sqlSource.getBoundSql(obj);
                String sql = boundsql.getSql();
                String sqlparts[] = sql.split("where");
                String newsql = sqlparts[0] + ",version=version+1  where" + sqlparts[1] + " and version=" + version;

                BoundSql myBoundSql = new BoundSql(ms.getConfiguration(), newsql, boundsql.getParameterMappings(),
                        boundsql.getParameterObject());

                MappedStatement newms = copyFromMappedStatement(ms, new MyBoundSqlSource(myBoundSql));
                invocation.getArgs()[0] = newms;
                Object num = invocation.proceed();
                if (num != null && !"1".equals(num.toString())) {
                    throw new RuntimeException("乐观锁版本不对");
                }
                return num;
            }
            return invocation.proceed();
        } else {
            return invocation.proceed();
        }
    }

    public static Object getFieldValue(Object obj, String fieldName) {
        Object result = null;
        Field field = getField(obj, fieldName);
        if (field != null) {
            field.setAccessible(true);
            try {
                result = field.get(obj);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Field getField(Object obj, String fieldName) {
        Field field = null;
        for (Class<?> clazz = obj.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
            try {
                field = clazz.getDeclaredField(fieldName);
                break;
            } catch (NoSuchFieldException e) {
                // 这里不用做处理，子类没有该字段可能对应的父类有，都没有就返回null。
            }
        }
        return field;
    }

    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource,
                ms.getSqlCommandType());

        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        String[] keyProperties = ms.getKeyProperties();
        if (keyProperties != null) {
            builder.keyProperty(StringUtils.join(keyProperties, ','));
        }

        // setStatementTimeout()
        builder.timeout(ms.getTimeout());

        // setStatementResultMap()
        builder.parameterMap(ms.getParameterMap());

        // setStatementResultMap()
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());

        // setStatementCache()
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());

        return builder.build();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    public static class MyBoundSqlSource implements SqlSource {

        private BoundSql boundSql;

        public MyBoundSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}