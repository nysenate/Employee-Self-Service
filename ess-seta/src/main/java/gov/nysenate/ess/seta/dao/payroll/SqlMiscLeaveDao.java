package gov.nysenate.ess.seta.dao.payroll;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.seta.model.payroll.MiscLeaveGrant;
import gov.nysenate.ess.seta.model.payroll.MiscLeaveType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public class SqlMiscLeaveDao extends SqlBaseDao implements MiscLeaveDao {

    @Override
    public List<MiscLeaveGrant> getMiscLeaveGrants(int empId) {
        return remoteNamedJdbc.query(SqlMiscLeaveQuery.GET_MISC_LEAVE_GRANTS.getSql(schemaMap()),
                new MapSqlParameterSource("empId", empId), miscLeaveGrantRowMapper);
    }

    private RowMapper<MiscLeaveGrant> miscLeaveGrantRowMapper = (rs, rowNum) ->
        new MiscLeaveGrant(
                rs.getInt("NUXREFEM"),
                MiscLeaveType.valueOfId(
                        Optional.ofNullable(rs.getBigDecimal("NUXRMISC"))
                                .map(BigDecimal::toBigInteger)
                                .orElse(null)
                ),
                getLocalDate(rs, "DTBEGIN"),
                getLocalDate(rs, "DTEND")
        );

}
