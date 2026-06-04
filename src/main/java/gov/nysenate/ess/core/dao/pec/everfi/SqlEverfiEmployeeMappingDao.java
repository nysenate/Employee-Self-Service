package gov.nysenate.ess.core.dao.pec.everfi;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.pec.everfi.EverfiEmployeeMapping;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static gov.nysenate.ess.core.dao.pec.everfi.SqlEverfiUserQuery.*;


@Repository
public class SqlEverfiEmployeeMappingDao extends SqlBaseDao implements EverfiEmployeeMappingDao {

    @Override
    public List<EverfiEmployeeMapping> findAll() {
        return localNamedJdbc.query(
                SELECT_ALL_MAPPINGS.getSql(schemaMap()),
                everfiEmployeeMappingRowMapper
        );
    }

    @Override
    public Optional<EverfiEmployeeMapping> findByEmpId(int empId) {
        return Optional.ofNullable(DataAccessUtils.singleResult(localNamedJdbc.query(
                SELECT_MAPPING_BY_EMP_ID.getSql(schemaMap()),
                new MapSqlParameterSource("employeeId", empId),
                everfiEmployeeMappingRowMapper
        )));
    }

    @Override
    public Optional<EverfiEmployeeMapping> findByEverfiUuid(String everfiUuid) {
        return Optional.ofNullable(DataAccessUtils.singleResult(localNamedJdbc.query(
                SELECT_MAPPING_BY_UUID.getSql(schemaMap()),
                new MapSqlParameterSource("everfiUuid", everfiUuid),
                everfiEmployeeMappingRowMapper
        )));
    }

    @Override
    public int insert(EverfiEmployeeMapping mapping) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("everfiUuid", mapping.everfiUuid())
                .addValue("employeeId", mapping.employeeId());
        return localNamedJdbc.update(INSERT_MAPPING.getSql(schemaMap()), params);
    }

    private static final RowMapper<EverfiEmployeeMapping> everfiEmployeeMappingRowMapper = (rs, rowNum) ->
            new EverfiEmployeeMapping(
                    rs.getInt("emp_id"),
                    rs.getString("everfi_uuid")
            );

}
