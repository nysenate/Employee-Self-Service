package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SqlTravelApplicationStatusDao extends SqlBaseDao {

    public void saveTravelApplicationStatus(TravelApplicationStatus status, int appVersionId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("appVersionId", appVersionId)
                .addValue("createdDateTime", toDate(status.dateTime()))
                .addValue("status", status.status.name())
                .addValue("note", status.note());
        String sql = SqlTravelApplicationStatusQuery.INSERT_STATUS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlTravelApplicationStatusQuery implements BasicSqlQuery {
        INSERT_STATUS(
                "INSERT INTO ${travelSchema}.app_version_status(app_version_id, created_date_time, status, note)\n" +
                        " VALUES(:appVersionId, :createdDateTime, :status, :note)"
        );

        private String sql;

        SqlTravelApplicationStatusQuery(String sql) {
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
