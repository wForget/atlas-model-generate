package cn.wangz.atlas.model.annotation;

import org.springframework.core.annotation.AliasFor;

public @interface AtlasRelationshipAttribute {

    @AliasFor("name")
    String value() default "";

    @AliasFor("value")
    String name() default "";

    String relationShipName() default "";

}
