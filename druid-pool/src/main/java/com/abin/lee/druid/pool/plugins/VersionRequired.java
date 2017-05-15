package com.abin.lee.druid.pool.plugins;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表明此类需要乐观锁控制 条件1:sql是update语句 2:参数的类上标记有VersionRequired的注解 3:参数不为空
 * 4:参数里面有version字段则走乐观锁验证
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface VersionRequired {
}

