package io.jcloud.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.junit.jupiter.api.extension.ExtendWith;

import io.jcloud.api.conditions.DisabledOnQuarkusVersionCondition;

/**
 * The {@code @DisabledOnQuarkus} annotation can be used to selectively enable or disable certain tests based on version
 * of Quarkus used in the test suite.
 *
 * @see #version()
 * @see #reason()
 */
@Inherited
@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(DisabledOnQuarkusVersionCondition.class)
@Repeatable(DisabledOnQuarkusVersions.class)
public @interface DisabledOnQuarkusVersion {
    /**
     * Regular expression that is matched against the version of Quarkus used in the test suite. If the version matches,
     * the test is disabled. Note that the entire Quarkus version string must match, substring match isn't enough.
     */
    String version();

    /**
     * Why is the annotated test class or test method disabled.
     */
    String reason();
}
