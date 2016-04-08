package com.patterncat.rpc.scan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 服务提供方发布服务的注解
 * Created by patterncat on 2016/4/8.
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
//@Component
public @interface ServiceExporter {
    //服务发现用的唯一标识，用于服务自动寻址
    String value() default "";
    String debugAddress() default "";
}
