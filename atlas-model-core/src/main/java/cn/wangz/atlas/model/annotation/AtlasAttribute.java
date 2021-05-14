package cn.wangz.atlas.model.annotation;

import org.springframework.core.annotation.AliasFor;

public @interface AtlasAttribute {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

}
