package org.oagi.score.repo.api.security;

import org.oagi.score.repo.api.base.ScoreRole;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
public @interface AccessControl {

    ScoreRole[] requiredAnyRole() default {};

}
