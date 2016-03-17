package gov.nysenate.ess.supply.shipment.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.supply.shipment.ShipmentStatus;
import gov.nysenate.ess.supply.shipment.ShipmentVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
public class SqlShipmentVersionDao extends SqlBaseDao implements ShipmentVersionDao {

    @Autowired private EmployeeInfoService employeeService;

    @Override
    public int insertVersion(ShipmentVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("issuingEmpId", version.getIssuingEmployee().isPresent() ? version.getIssuingEmployee().get() : null)
                .addValue("status", version.getStatus().toString())
                .addValue("createdEmpId", version.getModifiedBy().getEmployeeId());
        String sql = SqlShipmentVersionQuery.INSERT_SHIPMENT_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    @Override
    public ShipmentVersion getVersionById(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId);
        String sql = SqlShipmentVersionQuery.GET_VERSION_BY_ID.getSql(schemaMap());
        return localNamedJdbc.queryForObject(sql, params, new ShipmentVersionMapper(employeeService));
    }

    private enum SqlShipmentVersionQuery implements BasicSqlQuery {

        INSERT_SHIPMENT_VERSION(
                "INSERT INTO ${supplySchema}.shipment_version(issuing_emp_id, status, created_emp_id) \n" +
                "VALUES (:issuingEmpId, :status::${supplySchema}.shipment_status, :createdEmpId)"
        ),
        GET_VERSION_BY_ID(
                "SELECT version_id, issuing_emp_id, status, created_emp_id \n" +
                "FROM ${supplySchema}.shipment_version \n" +
                "WHERE version_id = :versionId"
        );

        SqlShipmentVersionQuery(String sql) {
            this.sql = sql;
        }

        private String sql;

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class ShipmentVersionMapper extends BaseRowMapper<ShipmentVersion> {

        private EmployeeInfoService employeeInfoService;

        public ShipmentVersionMapper(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
        }

        @Override
        public ShipmentVersion mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("version_id");
            ShipmentStatus status  = ShipmentStatus.valueOf(rs.getString("status"));
            int issueEmpId = rs.getInt("issuing_emp_id");
            Employee issueEmp = null;
            if (issueEmpId != 0) {
                issueEmp = employeeInfoService.getEmployee(rs.getInt("issuing_emp_id"));
            }
            Employee modifiedEmp = employeeInfoService.getEmployee(rs.getInt("created_emp_id"));
            return new ShipmentVersion.Builder().withId(id).withIssuingEmployee(issueEmp)
                    .withModifiedBy(modifiedEmp).withStatus(status).build();
        }
    }
}
