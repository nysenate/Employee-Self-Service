package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;
import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

@Repository
public class SqlAmendmentStatusDao extends SqlBaseDao {

    /**
     * @param status The status to be saved.
     * @param amendmentId The id of the amendment this status belongs to.
     */
    public void saveAmendmentStatus(TravelApplicationStatus status, int amendmentId) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("amendmentId", amendmentId)
                .addValue("createdDateTime", toDate(status.dateTime()))
                .addValue("status", status.status.name())
                .addValue("note", status.note());
        String sql = SqlTravelApplicationStatusQuery.INSERT_STATUS.getSql(schemaMap());
        localNamedJdbc.update(sql, params);
    }

    private enum SqlTravelApplicationStatusQuery implements BasicSqlQuery {
        INSERT_STATUS(
                "INSERT INTO ${travelSchema}.amendment_status(amendment_id, created_date_time, status, note)\n" +
                        " VALUES(:amendmentId, :createdDateTime, :status, :note)"
        );

        private final String sql;

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
