package cn.wangz.atlas.model.annotation;

public @interface AtlasRelationshipAttribute {

    String name() default "";

    String relationShipName() default "";

}
