package gov.nysenate.ess.supply.requisition.dao;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.supply.requisition.RequisitionVersion;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class SqlRequisitionVersionDao extends SqlBaseDao {

    protected int insertRequisitionVersion(RequisitionVersion version) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("customerId", version.getCustomer().getEmployeeId())
                .addValue("destination", version.getDestination().getLocId().toString())
                .addValue("status", version.getStatus().toString())
                .addValue("issuingEmpId", version.getIssuer().map(Employee::getEmployeeId).orElse(null))
                .addValue("createdEmpId", version.getModifiedBy().getEmployeeId())
                .addValue("note", version.getNote().orElse(null));
        String sql = SqlRequisitionVersionQuery.INSERT_REQUISITION_VERSION.getSql(schemaMap());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        localNamedJdbc.update(sql, params, keyHolder);
        return (Integer) keyHolder.getKeys().get("version_id");
    }

    private enum SqlRequisitionVersionQuery implements BasicSqlQuery {
        INSERT_REQUISITION_VERSION(
                "INSERT INTO ${supplySchema}.requisition_version(customer_id, destination, status, issuing_emp_id, \n" +
                "created_emp_id, note) VALUES (:customerId, :destination, :status::${supplySchema}.requisition_status, \n" +
                ":issuingEmpId, :createdEmpId, :note)"
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
}
