package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiUserIDs;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;

import static gov.nysenate.ess.core.dao.pec.everfi.SqlEverfiUserQuery.*;


@Repository
public class SqlEverfiUserDao extends SqlBaseDao implements EverfiUserDao {

    public EverfiUserIDs getEverfiUserIDsWithEmpID(int empID) {
        try {
            List<EverfiUserIDs> everfiUserIDsList =  localNamedJdbc.query(
                    SELECT_EMP_BY_EMP_ID.getSql(schemaMap()),
                    new MapSqlParameterSource("emp_id", empID),
                    everfiUserIDsRowMapper
            );
            if (everfiUserIDsList.isEmpty() || everfiUserIDsList == null) {
                throw new IncorrectResultSizeDataAccessException(0);
            }
            else {
                return everfiUserIDsList.get(0);
            }
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public EverfiUserIDs getEverfiUserIDsWithEverfiUUID(String everfiUUID) {
        try {
            return localNamedJdbc.queryForObject(
                    SELECT_EMP_BY_EVERFI_ID.getSql(schemaMap()),
                    new MapSqlParameterSource("everfi_UUID", everfiUUID),
                    everfiUserIDsRowMapper
            );
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<EverfiUserIDs> getIgnoredEverfiUserIDs() {
        try {
            return localNamedJdbc.query(
                    SELECT_IGNORED_EVERFI_USER_IDS.getSql(schemaMap()), everfiUserIDsRowMapper
            );
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int insertEverfiUserIDs(String everfiUUID, Integer empID) throws DuplicateKeyException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("everfi_UUID", everfiUUID);
        params.addValue("emp_id", empID);
        return localNamedJdbc.update(INSERT_EVERFI_USER_ID.getSql(schemaMap()), params);
    }

    public int everfiUserIDCount() {
        return localJdbc.queryForObject(
                COUNT_EVERFI_USER_IDS.getSql(schemaMap()), Integer.class);
    }

    public void insertIgnoredID(String everfiUUID, Integer empID) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("everfi_UUID", everfiUUID);
        params.addValue("emp_id", empID);
        localNamedJdbc.update(INSERT_IGNORED_EVERFI_USER_ID.getSql(schemaMap()), params);
    }

    public void removeIgnoredID(String everfiUUID) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("everfi_UUID", everfiUUID);
        localNamedJdbc.update(REMOVE_IGNORED_EVERFI_USER_ID.getSql(schemaMap()), params);
    }

    private static final RowMapper<EverfiUserIDs> everfiUserIDsRowMapper = (rs, rowNum) ->
            new EverfiUserIDs(
                    rs.getInt("emp_id"),
                    rs.getString("everfi_uuid")
            );

}
