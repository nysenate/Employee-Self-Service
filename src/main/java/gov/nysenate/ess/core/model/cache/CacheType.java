package gov.nysenate.ess.core.model.cache;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.period.PayPeriodType;

import java.util.Arrays;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum CacheType {
    EMPLOYEE(Integer.class),
    ACCRUAL_ANNUAL(Integer.class),
    HOLIDAY(String.class),
    PAY_PERIOD(PayPeriodType.class),
    TRANSACTION(Integer.class),
    SUPERVISOR_EMP_GROUP(Integer.class),
    ACTIVE_TIME_RECORDS(Integer.class),
    LOCATION(String.class),
    PERSONNEL_TASK(Integer.class);

    private final Class<?> keyType;

    CacheType(Class<?> keyType) {
        this.keyType = keyType;
    }

    public Object getTypedKey(String key) {
        if (String.class == keyType) {
            return key;
        }
        if (Integer.class == keyType) {
            try {
                return Integer.parseInt(key);
            } catch (NumberFormatException ex) {
                throw new InvalidRequestParamEx(key, "key", "String",
                        "Keys for cache " + this + " must be parsable into an integer");
            }
        }
        if (PayPeriodType.class == keyType) {
            return PayPeriodType.valueOf(key);
        }
        throw new InvalidRequestParamEx(name(), "cacheName", "String",
                "Element evict is not currently supported for " + this);
    }
}