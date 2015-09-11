package gov.nysenate.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that a source file is currently in a state of work in progress and may not be fully
 * implemented and/or operational.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface WorkInProgress
{
    String author() default "";
    String since() default "";
    String desc() default "";
}