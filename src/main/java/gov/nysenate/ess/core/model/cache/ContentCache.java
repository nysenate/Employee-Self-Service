package gov.nysenate.ess.core.model.cache;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.travel.provider.gsa.GsaResponseId;

import java.util.Arrays;

/**
 * Content caches store various types of data. The cache types enumerated here should
 * be able to manage themselves, have configurable sizes, and have functionality to warm
 * up upon request.
 */
public enum ContentCache
{
    EMPLOYEE(Integer.class),
    ACCRUAL_ANNUAL(Integer.class),
    HOLIDAY(String.class),
    PAY_PERIOD(PayPeriodType.class),
    TRANSACTION(Integer.class),
    SUPERVISOR_EMP_GROUP(Integer.class),
    ACTIVE_TIME_RECORDS(Integer.class),
    LOCATION(String.class),
    GSA_API(GsaResponseId.class),
    PERSONNEL_TASK(Integer.class),
    ;

    private Class<?> keyType;

    ContentCache(Class<?> keyType) {
        this.keyType = keyType;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    private static final ImmutableSet<ContentCache> allContentCaches =
            Sets.immutableEnumSet(Arrays.asList(ContentCache.values()));

    public static ImmutableSet<ContentCache> getAllContentCaches() {
        return allContentCaches;
    }
}