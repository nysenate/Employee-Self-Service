package gov.nysenate.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the test will require the database to be in a certain state to pass.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface TestDependsOnDatabase
{
}
