package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.unit.Location;
import gov.nysenate.ess.core.model.unit.LocationId;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.unit.LocationService;
import gov.nysenate.ess.supply.item.LineItem;
import gov.nysenate.ess.supply.requisition.RequisitionStatus;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

@Repository
public class SqlRequisitionVersionDao extends SqlBaseDao {

    @Autowired private SqlReqLineItemDao lineItemDao;
    @Autowired private EmployeeInfoService employeeInfoService;
    @Autowired private LocationService locationService;

    protected int insertRequisitionVersion(RequisitionVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", version.getDestination().getLocId().toString())
                .addValue("status", version.getStatus().toString())
                .addValue("issuingEmpId", version.getIssuer().map(Employee::getEmployeeId).orElse(null))
                .addValue("createdEmpId", version.getCreatedBy().getEmployeeId())
                .addValue("note", version.getNote().orElse(null));
        String sql = SqlRequisitionVersionQuery.INSERT_REQUISITION_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    protected RequisitionVersion getVersionById(int versionId) {
        MapSqlParameterSource params = new MapSqlParameterSource("versionId", versionId);
        String sql = SqlRequisitionVersionQuery.GET_VERSION_BY_ID.getSql(schemaMap());
        RequisitionVersionRowMapper rowMapper = new RequisitionVersionRowMapper(lineItemDao, employeeInfoService, locationService);
        return localNamedJdbc.queryForObject(sql, params, rowMapper);
    }

    private enum SqlRequisitionVersionQuery implements BasicSqlQuery {
        INSERT_REQUISITION_VERSION(
                "INSERT INTO ${supplySchema}.requisition_version(customer_id, destination, status, issuing_emp_id, \n" +
                "created_emp_id, note) VALUES (:customerId, :destination, :status::${supplySchema}.requisition_status, \n" +
                ":issuingEmpId, :createdEmpId, :note)"
        ),
        GET_VERSION_BY_ID(
                "SELECT version_id, customer_id, destination, status, issuing_emp_id, created_emp_id, note \n" +
                "FROM ${supplySchema}.requisition_version \n" +
                "WHERE version_id = :versionId"
        );

        private String sql;

        SqlRequisitionVersionQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private class RequisitionVersionRowMapper extends BaseRowMapper<RequisitionVersion> {

        private SqlReqLineItemDao lineItemDao;
        private EmployeeInfoService employeeInfoService;
        private LocationService locationService;

        RequisitionVersionRowMapper(SqlReqLineItemDao lineItemDao, EmployeeInfoService employeeInfoService,
                                    LocationService locationService) {
            this.lineItemDao = lineItemDao;
            this.employeeInfoService = employeeInfoService;
            this.locationService = locationService;
        }

        @Override
        public RequisitionVersion mapRow(ResultSet rs, int i) throws SQLException {
            int id = rs.getInt("version_id");
            Employee customer = getEmployeeIfExists(rs.getInt("customer_id"));
            Location destination = locationService.getLocation(LocationId.ofString(rs.getString("destination")));
            RequisitionStatus status = RequisitionStatus.valueOf(rs.getString("status"));
            Employee issuer = getEmployeeIfExists(rs.getInt("issuing_emp_id"));
            Employee createdBy = getEmployeeIfExists(rs.getInt("created_emp_id"));
            String note = rs.getString("note");
            Set<LineItem> lineItems = lineItemDao.getLineItems(id);
            return new RequisitionVersion.Builder()
                    .withId(id)
                    .withCustomer(customer)
                    .withDestination(destination)
                    .withStatus(status)
                    .withIssuer(issuer)
                    .withCreatedBy(createdBy)
                    .withNote(note)
                    .withLineItems(lineItems)
                    .build();
        }

        private Employee getEmployeeIfExists(int empId) {
            if (empId != 0) {
                return employeeInfoService.getEmployee(empId);
            }
            return null;
        }
    }
}
