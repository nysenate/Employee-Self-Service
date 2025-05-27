package gov.nysenate.ess.supply.employee;

import com.google.common.collect.Sets;
import gov.nysenate.ess.core.dao.base.BaseHandler;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Repository
public class SupplyEmployeeDao extends SqlBaseDao {

    private EmployeeInfoService employeeInfoService;

    @Autowired
    public SupplyEmployeeDao(EmployeeInfoService employeeInfoService) {
        this.employeeInfoService = employeeInfoService;
    }

    /**
     * Get all employees who have ever issued a requisition in the electronic supply app.
     * @return
     */
    public Set<Employee> getDistinctIssuers() {
        String sql = SqlSupplyEmployeeQuery.GET_DISTINCT_ISSUERS.getSql(schemaMap());
        SupplyEmployeeHandler empHandler = new SupplyEmployeeHandler(employeeInfoService);
        localNamedJdbc.query(sql, empHandler);
        return empHandler.getResults();
    }

    private enum SqlSupplyEmployeeQuery implements BasicSqlQuery {
        GET_DISTINCT_ISSUERS(
                "SELECT DISTINCT(issuing_emp_id)\n" +
                "FROM ${supplySchema}.requisition_content rc\n" +
                "  JOIN ${supplySchema}.requisition r ON r.current_revision_id = rc.revision_id\n" +
                "WHERE issuing_emp_id is not NULL"
        );

        private final String sql;

        SqlSupplyEmployeeQuery(String sql) {
            this.sql = sql;
        }

        @Override
        public String getSql() {
            return this.sql;
        }

        @Override
        public DbVendor getVendor() {
            return DbVendor.POSTGRES;
        }
    }

    private static class SupplyEmployeeHandler extends BaseHandler {

        private Set<Integer> empIds;
        private EmployeeInfoService employeeInfoService;

        public SupplyEmployeeHandler(EmployeeInfoService employeeInfoService) {
            this.employeeInfoService = employeeInfoService;
            this.empIds = new HashSet<>();
        }

        @Override
        public void processRow(ResultSet rs) throws SQLException {
            empIds.add(rs.getInt("issuing_emp_id"));
        }

        protected Set<Employee> getResults() {
            return Sets.newHashSet(employeeInfoService.getEmployees(empIds).values());
        }
    }
}
