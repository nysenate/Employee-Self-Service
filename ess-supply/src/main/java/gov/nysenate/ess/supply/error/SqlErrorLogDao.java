package gov.nysenate.ess.supply.error;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SqlErrorLogDao extends SqlBaseDao {

   public void insertErrorMessage(String message) {
      MapSqlParameterSource params = new MapSqlParameterSource("message", message);
      String sql = SqlErrorLogQuery.INSERT_ERROR_MESSAGE.getSql(schemaMap());
      localNamedJdbc.update(sql, params);
   }

   private enum SqlErrorLogQuery implements BasicSqlQuery {
      INSERT_ERROR_MESSAGE(
              "INSERT INTO ${supplySchema}.error_log(message) \n" +
              "VALUES (:message)"
      );

      private String sql;

      SqlErrorLogQuery(String sql) {
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
}
