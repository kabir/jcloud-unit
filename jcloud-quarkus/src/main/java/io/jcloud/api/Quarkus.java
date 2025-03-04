package io.jcloud.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Quarkus {

    /**
     * Specify the path location where the Quarkus application module is located. By default, it will use the current
     * module.
     */
    String location() default ".";

    // By default, it will load all the classes in the classpath.
    Class<?>[] classes() default {};

    /**
     * Add forced dependencies.
     */
    Dependency[] dependencies() default {};
}
