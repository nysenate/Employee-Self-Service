package gov.nysenate.ess.time.dao.payroll;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.time.model.payroll.MiscLeaveGrant;
import gov.nysenate.ess.time.model.payroll.MiscLeaveType;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static gov.nysenate.ess.time.dao.payroll.SqlMiscLeaveQuery.GET_MISC_LEAVE_GRANTS;
import static gov.nysenate.ess.time.dao.payroll.SqlMiscLeaveQuery.GET_SICK_LEAVE_GRANTS;

@Repository
public class SqlMiscLeaveDao extends SqlBaseDao implements MiscLeaveDao {
    @Override
    public List<MiscLeaveGrant> getMiscLeaveGrants(int empId) {
        var param = new MapSqlParameterSource("empId", empId);
        List<MiscLeaveGrant> miscLeaveList = remoteNamedJdbc.query(
                GET_MISC_LEAVE_GRANTS.getSql(schemaMap()),
                param, miscLeaveGrantRowMapper);
        miscLeaveList.addAll(remoteNamedJdbc.query(
                GET_SICK_LEAVE_GRANTS.getSql(schemaMap()),
                param, donatedSickLeaveGrantRowMapper));
        return miscLeaveList;
    }

    private final RowMapper<MiscLeaveGrant> miscLeaveGrantRowMapper = (rs, rowNum) ->
        new MiscLeaveGrant(
                rs.getInt("NUXREFEM"),
                MiscLeaveType.valueOfId(
                        Optional.ofNullable(rs.getBigDecimal("NUXRMISC"))
                                .map(BigDecimal::toBigInteger)
                                .orElse(null)
                ),
                getLocalDate(rs, "DTBEGIN"),
                getLocalDate(rs, "DTEND"),
                null);

    private final RowMapper<MiscLeaveGrant> donatedSickLeaveGrantRowMapper = (rs, rowNum) ->
            new MiscLeaveGrant(rs.getInt("NUXREFEM"), MiscLeaveType.LEAVE_DONATION,
            getLocalDate(rs, "DTEFFECT"), getLocalDate(rs, "DTEND"),
            rs.getBigDecimal("NUAPPROVEHRS"));
}
