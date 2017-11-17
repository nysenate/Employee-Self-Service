package gov.nysenate.ess.travel.application.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.model.EmployeeRequestorInfo;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;

@Repository
@Transactional(value = "localTxManager")
public class SqlUserConfigDao extends SqlBaseDao {

   public EmployeeRequestorInfo getRequestorInfoById(int empId){
       MapSqlParameterSource params = new MapSqlParameterSource().addValue("empId", empId);

       String sql = SqlUserConfigDaoQuery.GET_REQUESTOR_INFO.getSql(schemaMap());
       return localNamedJdbc.query(sql, params, new EmployeeRequestorInfoRowMapper()).get(0);
   }

    private enum SqlUserConfigDaoQuery implements BasicSqlQuery {
        GET_REQUESTOR_INFO("SELECT * FROM ${travelSchema}.travel_requestors WHERE empId=:empId");

        private String sql;

        SqlUserConfigDaoQuery(String sql) {
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

   private class EmployeeRequestorInfoRowMapper extends BaseRowMapper<EmployeeRequestorInfo> {

       @Override
       public EmployeeRequestorInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
           return new EmployeeRequestorInfo(rs.getInt("empId"), rs.getInt("requestorId"),
                   rs.getDate("startDate"), rs.getDate("endDate"));
       }
   }
}
