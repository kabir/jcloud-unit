package io.jcloud.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import io.jcloud.api.conditions.DisabledOnQuarkusVersionCondition;

@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DisabledOnQuarkusVersionCondition.class)
public @interface DisabledOnQuarkusVersions {
    DisabledOnQuarkusVersion[] value();
}
