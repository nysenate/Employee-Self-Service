package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Range;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import static gov.nysenate.ess.time.dao.accrual.SqlDonationQuery.INSERT_DONATION;
import static gov.nysenate.ess.time.dao.accrual.SqlDonationQuery.SELECT_DONATIONS_IN_RANGE;

@Service
public class SqlDonationDao extends SqlBaseDao implements DonationDao {
    @Override
    public Multimap<LocalDate, BigDecimal> getDonatedTime(int empId, Range<LocalDate> dateRange) {
        if (dateRange == null) {
            return ArrayListMultimap.create();
        }
        var params = new MapSqlParameterSource("empId", empId)
                .addValue("startDate", dateRange.lowerEndpoint())
                .addValue("endDate", dateRange.upperEndpoint());
        var rch = new MapDonationRecords();
        remoteNamedJdbc.query(SELECT_DONATIONS_IN_RANGE.getSql(schemaMap()), params, rch);
        return rch.resultsMap;
    }

    @Override
    public boolean submitDonation(Employee emp, BigDecimal donation) {
        var params = new MapSqlParameterSource("effectiveDate", LocalDate.now())
                .addValue("empId", emp.getEmployeeId())
                .addValue("uid", emp.getUid())
                .addValue("donation", donation);
        return remoteNamedJdbc.update(INSERT_DONATION.getSql(schemaMap()), params) != 0;
    }

    private static final class MapDonationRecords implements RowCallbackHandler {
        private final Multimap<LocalDate, BigDecimal> resultsMap = ArrayListMultimap.create();

        @Override
        public void processRow(@Nonnull ResultSet rs) throws SQLException {
            resultsMap.put(getLocalDate(rs, "donationDate"), rs.getBigDecimal("donationHours"));
        }
    }
}
