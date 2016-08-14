package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.Range;
import com.google.common.collect.RangeSet;
import com.google.common.eventbus.EventBus;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.service.period.PayPeriodService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.core.util.SortOrder;
import gov.nysenate.ess.core.model.period.PayPeriod;
import gov.nysenate.ess.core.model.period.PayPeriodType;
import gov.nysenate.ess.time.dao.accrual.AccrualDao;
import gov.nysenate.ess.time.dao.attendance.AttendanceDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import gov.nysenate.ess.core.model.cache.ContentCache;
import gov.nysenate.ess.core.service.cache.EhCacheManageService;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;

/**
 * A service that provides accrual information
 */
@Service
public class EssCachedAccrualInfoService implements AccrualInfoService, CachingService<Integer>
{
    private static final Logger logger = LoggerFactory.getLogger(EssCachedAccrualInfoService.class);

    @Autowired private AccrualDao accrualDao;
    @Autowired private AttendanceDao attendanceDao;

    @Autowired private PayPeriodService payPeriodService;

    @Autowired private EhCacheManageService cacheManageService;
    @Autowired private EventBus eventBus;

    private Cache annualAccrualCache;

    @PostConstruct
    public void init() {
        eventBus.register(this);
        setupCaches();
    }

    public void setupCaches() {
        this.annualAccrualCache = cacheManageService.registerEternalCache(getCacheType().name());
    }

    /**
     * A data type used to store an employees annual accrual summaries
     * The summaries are stored as a map of year->summary
     */
    private static final class AnnualAccCacheTree {
        TreeMap<Integer, AnnualAccSummary> annualAccruals;

        AnnualAccCacheTree(TreeMap<Integer, AnnualAccSummary> annualAccruals) {
            this.annualAccruals = annualAccruals;
        }

        TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int endYear) {
            return new TreeMap<>(annualAccruals.headMap(endYear, true));
        }
    }

    /** --- Accrual Info Service Implemented Methods ---
     * @see AccrualInfoService*/

    /** {@inheritDoc} */
    @Override
    public TreeMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        annualAccrualCache.acquireReadLockOnKey(empId);
        Element elem = annualAccrualCache.get(empId);
        annualAccrualCache.releaseReadLockOnKey(empId);
        AnnualAccCacheTree cachedAccTree;
        if (elem == null) {
            TreeMap<Integer, AnnualAccSummary> annualAccruals =
                accrualDao.getAnnualAccruals(empId, DateUtils.THE_FUTURE.getYear());
            cachedAccTree = new AnnualAccCacheTree(annualAccruals);
            putAnnualAccTreeInCache(empId, cachedAccTree);
        }
        else {
            cachedAccTree = (AnnualAccCacheTree) elem.getObjectValue();
        }
        return cachedAccTree.getAnnualAccruals(endYear);
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getActiveAttendancePeriods(int empId, LocalDate endDate, SortOrder dateOrder) {
        TreeMap<Integer, AnnualAccSummary> annAcc = getAnnualAccruals(empId, endDate.getYear());
        Optional<Integer> openYear = annAcc.descendingMap().entrySet().stream()
                .filter(e -> e.getValue().getCloseDate() == null)
                .map(Map.Entry::getKey)
                .findFirst();
        if (openYear.isPresent()) {
            return payPeriodService.getPayPeriods(
                PayPeriodType.AF, Range.closed(LocalDate.of(openYear.get(), 1, 1), endDate), dateOrder);
        }
        return new ArrayList<>();
    }

    /** {@inheritDoc} */
    @Override
    public List<PayPeriod> getOpenPayPeriods(PayPeriodType type, Integer empId, SortOrder dateOrder) {
        RangeSet<LocalDate> openDates = attendanceDao.getOpenDates(empId);
        return openDates.isEmpty() ? Collections.emptyList() : payPeriodService.getPayPeriods(type, openDates.span(), dateOrder);
    }

    /** --- Caching Service Implemented Methods ---
     * @see CachingService*/

    /** {@inheritDoc} */
    @Override
    public ContentCache getCacheType() {
        return ContentCache.ACCRUAL_ANNUAL;
    }

    /** {@inheritDoc} */
    @Override
    public void evictContent(Integer empId) {
        annualAccrualCache.remove(empId);
    }

    /** {@inheritDoc} */
    @Override
    public void evictCache() {
        logger.info("Clearing {} cache..", getCacheType());
        annualAccrualCache.removeAll();
    }

    /** {@inheritDoc} */
    @Override
    public void warmCache() {
        // This cache doesn't get warmed
    }

    /** --- Internal Methods --- */

    private void putAnnualAccTreeInCache(int empId, AnnualAccCacheTree annualAccCacheTree) {
        annualAccrualCache.acquireWriteLockOnKey(empId);
        annualAccrualCache.put(new Element(empId, annualAccCacheTree));
        annualAccrualCache.releaseWriteLockOnKey(empId);
    }
}