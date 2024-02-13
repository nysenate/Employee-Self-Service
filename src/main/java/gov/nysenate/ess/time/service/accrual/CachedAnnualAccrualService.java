package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.ImmutableSortedMap;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.cache.EmployeeCache;
import gov.nysenate.ess.core.service.personnel.ActiveEmployeeIdService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.dao.accrual.AccrualDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

class CachedAnnualAccrualService extends EmployeeCache<CachedAnnualAccrualService.AnnualAccCacheTree> {
    private static final Logger logger = LoggerFactory.getLogger(CachedAnnualAccrualService.class);
    private final AccrualDao accrualDao;
    private LocalDateTime lastUpdateDateTime;
    private final ActiveEmployeeIdService employeeIdService;

    @Autowired
    CachedAnnualAccrualService(AccrualDao accrualDao, ActiveEmployeeIdService employeeIdService) {
        this.accrualDao = accrualDao;
        lastUpdateDateTime = LocalDateTime.now();
        this.employeeIdService = employeeIdService;
    }

    @Override
    protected void warmCache() {
        for (var empId : employeeIdService.getActiveEmployeeIds()) {
            putId(empId);
        }
    }

    /* Fetch and add to the cache Annual Accruals for the given employee id. */
    private void putId(int empId) {
        var annualAccCacheTree = new AnnualAccCacheTree(accrualDao.getAnnualAccruals(empId, DateUtils.THE_FUTURE.getYear()));
        cache.put(empId, annualAccCacheTree);
    }

    /**
     * Used to prevent type erasure. The summaries are stored as a map of year -> summary
     */
    static final class AnnualAccCacheTree extends TreeMap<Integer, AnnualAccSummary> {
        private AnnualAccCacheTree(TreeMap<Integer, AnnualAccSummary> annualAccruals) {
            super(annualAccruals);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.ACCRUAL_ANNUAL;
    }

    ImmutableSortedMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        if (!cache.containsKey(empId)) {
            putId(empId);
        }
        return ImmutableSortedMap.copyOf(cache.get(empId).headMap(endYear, true));
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.accruals:60000}")
    private void updateAnnualAccCache() {
        logger.info("Checking for annual accrual record updates since {}", lastUpdateDateTime);
        List<AnnualAccSummary> updatedAnnualAccs = accrualDao.getAnnualAccsUpdatedSince(lastUpdateDateTime);
        for (var summary : updatedAnnualAccs) {
            var tree = cache.get(summary.getEmpId());
            if (tree != null) {
                tree.put(summary.getYear(), summary);
            }
        }
        lastUpdateDateTime = updatedAnnualAccs.stream().map(AnnualAccSummary::getUpdateDate)
                .max(LocalDateTime::compareTo).orElse(lastUpdateDateTime);
        logger.info("Refreshed cache with {} updated annual accrual records", updatedAnnualAccs.size());
    }
}
