package gov.nysenate.ess.travel.application.dao;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.travel.application.model.EmployeeRequestorInfo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Repository
public class SqlUserConfigDao extends SqlBaseDao {

   public EmployeeRequestorInfo getRequestorInfoById(int empId){
       MapSqlParameterSource params = new MapSqlParameterSource().addValue("empId", empId);

       String sql = SqlUserConfigDaoQuery.GET_REQUESTOR_INFO.getSql(schemaMap());
       EmployeeRequestorInfo employeeRequestorInfo;

       try {
           employeeRequestorInfo = localNamedJdbc.queryForObject(sql, params, new EmployeeRequestorInfoRowMapper());
       }
       catch(EmptyResultDataAccessException e){
           employeeRequestorInfo = new EmployeeRequestorInfo(empId);
       }
       return employeeRequestorInfo;
   }

   public void updateRequestorInfoById(int empId, int requestorId, Date startDate, Date endDate){
       MapSqlParameterSource params = new MapSqlParameterSource()
               .addValue("empId", empId)
               .addValue("requestorId", requestorId)
               .addValue("startDate", startDate)
               .addValue("endDate", endDate);
       String sql = SqlUserConfigDaoQuery.UPDATE_REQUSTOR_INFO.getSql(schemaMap());
       int rows = localNamedJdbc.update(sql, params);
       if(rows == 0){
           insertRequestorById(params);
       }
   }

   private void insertRequestorById(MapSqlParameterSource params){
       List<SqlParameterSource> paramList = new ArrayList<>();
       paramList.add(params);

       String sql = SqlUserConfigDaoQuery.INSERT_REQUESTOR_INFO.getSql(schemaMap());
       SqlParameterSource[] batchParams = new SqlParameterSource[paramList.size()];
       batchParams = paramList.toArray(batchParams);
       localNamedJdbc.batchUpdate(sql, batchParams);
   }

    private enum SqlUserConfigDaoQuery implements BasicSqlQuery {
        GET_REQUESTOR_INFO(
                "SELECT * FROM ${travelSchema}.travel_requestors WHERE emp_id=:empId"
        ),
        UPDATE_REQUSTOR_INFO(
                "UPDATE ${travelSchema}.travel_requestors\n" +
                "SET requestor_id=:requestorId, start_date=:startDate, end_date=:endDate\n" +
                "WHERE emp_id=:empId"
        ),
        INSERT_REQUESTOR_INFO(
                "INSERT INTO ${travelSchema}.travel_requestors\n" +
                        "VALUES (:empId, :requestorId, :startDate, :endDate)"
        );


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
           return new EmployeeRequestorInfo(rs.getInt("emp_id"), rs.getInt("requestor_id"),
                   rs.getDate("start_date"), rs.getDate("end_date"));
       }
   }
}
