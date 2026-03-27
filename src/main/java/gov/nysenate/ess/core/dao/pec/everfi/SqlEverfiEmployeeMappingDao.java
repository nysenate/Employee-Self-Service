package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.ess.core.dao.pec.everfi.SqlEverfiUserQuery.*;


@Repository
public class SqlEverfiEmployeeMappingDao extends SqlBaseDao implements EverfiEmployeeMappingDao {

    @Override
    public EverfiEmployeeMapping getEverfiUserIDsWithEmpID(int empID) {
        try {
            List<EverfiEmployeeMapping> everfiEmployeeMappingList = localNamedJdbc.query(
                    SELECT_EMP_BY_EMP_ID.getSql(schemaMap()),
                    new MapSqlParameterSource("emp_id", empID),
                    everfiUserIDsRowMapper
            );
            if (everfiEmployeeMappingList.isEmpty() || everfiEmployeeMappingList == null) {
                return null;
            } else {
                return everfiEmployeeMappingList.get(0);
            }
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public EverfiEmployeeMapping getEverfiUserIDsWithEverfiUUID(String everfiUUID) {
        try {
            return localNamedJdbc.queryForObject(
                    SELECT_EMP_BY_EVERFI_ID.getSql(schemaMap()),
                    new MapSqlParameterSource("everfi_UUID", everfiUUID),
                    everfiUserIDsRowMapper
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public int insertEverfiUserIDs(String everfiUUID, Integer empID) throws DuplicateKeyException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("everfi_UUID", everfiUUID);
        params.addValue("emp_id", empID);
        return localNamedJdbc.update(INSERT_EVERFI_USER_ID.getSql(schemaMap()), params);
    }

    @Override
    public int everfiUserIDCount() {
        return localJdbc.queryForObject(
                COUNT_EVERFI_USER_IDS.getSql(schemaMap()), Integer.class);
    }

    private static final RowMapper<EverfiEmployeeMapping> everfiUserIDsRowMapper = (rs, rowNum) ->
            new EverfiEmployeeMapping(
                    rs.getInt("emp_id"),
                    rs.getString("everfi_uuid")
            );

}
