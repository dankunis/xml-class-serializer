package io.github.dankunis.xml_serializer.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface XMLAttribute {
    String name() default "UNDEFINED";
    String tag() default "UNDEFINED";
}
