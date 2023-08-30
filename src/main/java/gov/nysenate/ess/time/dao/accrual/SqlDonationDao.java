package gov.nysenate.ess.time.dao.accrual;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

@Service
public class SqlDonationDao extends SqlBaseDao implements DonationDao {
    @Override
    public BigDecimal getTimeDonatedInLastYear(int empId) {
        var params = new MapSqlParameterSource("empId", empId)
                .addValue("startDate", LocalDate.now().minusYears(1))
                .addValue("endDate", LocalDate.now());
        return remoteNamedJdbc.query(SqlDonationQuery.SELECT_HOURS_DONATED_IN_RANGE.getSql(schemaMap),
                params, new HoursDonated());
    }

    @Override
    public Multimap<LocalDate, BigDecimal> getDonatedTime(int empId, int year) {
        var params = new MapSqlParameterSource("empId", empId).addValue("year", year);
        var rch = new MapDonationRecords();
        remoteNamedJdbc.query(SqlDonationQuery.SELECT_EMP_DONATION_RECORDS.getSql(schemaMap), params, rch);
        return rch.resultsMap;
    }

    @Override
    public boolean submitDonation(int empId, BigDecimal donation) {
        var params = new MapSqlParameterSource("effectiveDate", LocalDate.now())
                .addValue("empId", empId).addValue("donation", donation);
        return remoteNamedJdbc.update(SqlDonationQuery.INSERT_DONATION.getSql(schemaMap), params) != 0;
    }

    private static final class HoursDonated implements ResultSetExtractor<BigDecimal> {
        @Override
        public BigDecimal extractData(ResultSet rs) throws SQLException {
            rs.next();
            BigDecimal sum = rs.getBigDecimal("sum");
            return sum == null ? BigDecimal.ZERO : sum;
        }
    }

    private static final class MapDonationRecords implements RowCallbackHandler {
        private final Multimap<LocalDate, BigDecimal> resultsMap = ArrayListMultimap.create();

        @Override
        public void processRow(@Nonnull ResultSet rs) throws SQLException {
            resultsMap.put(getLocalDate(rs, "donationDate"), rs.getBigDecimal("donationHours"));
        }
    }
}
