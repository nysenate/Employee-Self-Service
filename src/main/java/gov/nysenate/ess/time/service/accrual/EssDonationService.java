package gov.nysenate.ess.time.service.accrual;

import com.google.common.collect.Range;
import gov.nysenate.ess.time.dao.accrual.DonationDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class EssDonationService implements DonationService {
    private final DonationDao donationDao;

    public EssDonationService(DonationDao donationDao) {
        this.donationDao = donationDao;
    }

    @Override
    public BigDecimal getHoursDonated(int empId, int year) {
        return donationDao.getDonatedTime(empId, year).values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public Map<LocalDate, BigDecimal> getHoursDonated(int empId, int startYear, LocalDate endDate) {
        Range<LocalDate> dateRange = Range.closed(LocalDate.ofYearDay(startYear, 1), endDate);
        return donationDao.getDonatedTime(empId, dateRange).asMap().entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey,
                        entry -> entry.getValue().stream().reduce(BigDecimal.ZERO, BigDecimal::add))
                );
    }
}
