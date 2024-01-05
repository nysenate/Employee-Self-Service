package gov.nysenate.ess.core.config;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * Marks a class whose subclasses are all services.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
@Inherited
public @interface InheritedService {}
