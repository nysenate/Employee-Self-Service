package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.ImmutableSortedMap;
import gov.nysenate.ess.core.model.cache.CacheType;
import gov.nysenate.ess.core.service.base.CachingService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.time.dao.accrual.AccrualDao;
import gov.nysenate.ess.time.model.accrual.AnnualAccSummary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

@Service
class CachedAnnualAccrualService extends CachingService<Integer, CachedAnnualAccrualService.AnnualAccCacheTree> {
    private static final Logger logger = LoggerFactory.getLogger(CachedAnnualAccrualService.class);
    private final AccrualDao accrualDao;
    private LocalDateTime lastUpdateDateTime;

    @Autowired
    CachedAnnualAccrualService(AccrualDao accrualDao) {
        this.accrualDao = accrualDao;
        lastUpdateDateTime = LocalDateTime.now();
    }

    /**
     * A data type used to store an employees annual accrual summaries
     * The summaries are stored as a map of year->summary
     */
    static final class AnnualAccCacheTree {
        private final TreeMap<Integer, AnnualAccSummary> annualAccruals;

        AnnualAccCacheTree(TreeMap<Integer, AnnualAccSummary> annualAccruals) {
            this.annualAccruals = annualAccruals;
        }

        ImmutableSortedMap<Integer, AnnualAccSummary> getAnnualAccruals(int endYear) {
            return ImmutableSortedMap.copyOf(
                    annualAccruals.headMap(endYear, true));
        }

        void updateAnnualAccSummary(AnnualAccSummary summary) {
            annualAccruals.put(summary.getYear(), summary);
        }
    }

    /** {@inheritDoc} */
    @Override
    public CacheType cacheType() {
        return CacheType.ACCRUAL_ANNUAL;
    }

    @Scheduled(fixedDelayString = "${cache.poll.delay.accruals:60000}")
    private void updateAnnualAccCache() {
        logger.info("Checking for annual accrual record updates since {}", lastUpdateDateTime);
        List<AnnualAccSummary> updatedAnnualAccs = accrualDao.getAnnualAccsUpdatedSince(lastUpdateDateTime);

        // process any updated records and get last update date time
        lastUpdateDateTime = updatedAnnualAccs.stream()
                .peek(this::updateAnnualAccSummary)
                .map(AnnualAccSummary::getUpdateDate)
                .max(LocalDateTime::compareTo)
                .orElse(lastUpdateDateTime);
        logger.info("Refreshed cache with {} updated annual accrual records", updatedAnnualAccs.size());
    }

    ImmutableSortedMap<Integer, AnnualAccSummary> getAnnualAccruals(int empId, int endYear) {
        AnnualAccCacheTree annualAccCacheTree = cache.get(empId);
        if (annualAccCacheTree == null) {
            TreeMap<Integer, AnnualAccSummary> annualAccruals =
                    accrualDao.getAnnualAccruals(empId, DateUtils.THE_FUTURE.getYear());
            annualAccCacheTree = new AnnualAccCacheTree(annualAccruals);
            cache.put(empId, annualAccCacheTree);
        }
        return annualAccCacheTree.getAnnualAccruals(endYear);
    }

    private void updateAnnualAccSummary(AnnualAccSummary annualAccSummary) {
        Optional.ofNullable(cache.get(annualAccSummary.getEmpId()))
                .ifPresent(cacheTree -> cacheTree.updateAnnualAccSummary(annualAccSummary));
    }
}
