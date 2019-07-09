package gov.nysenate.ess.core.dao.pec.video;

import gov.nysenate.ess.core.dao.base.BasicSqlQuery;
import gov.nysenate.ess.core.dao.base.DbVendor;

public enum SqlPECVideoQuery implements BasicSqlQuery {

    GET_PEC_VIDEOS_BASE("" +
            "SELECT *\n" +
            "FROM ${essSchema}.pec_video v\n" +
            "JOIN ${essSchema}.pec_video_code c\n" +
            "  ON v.id = c.pec_video_id"
    ),

    GET_PEC_VIDEOS("" +
            GET_PEC_VIDEOS_BASE.sql + "\n" +
            "WHERE (:activeOnly = FALSE OR v.active = TRUE)"
    ),

    GET_PEC_VIDEO_BY_ID("" +
            GET_PEC_VIDEOS_BASE.sql + "\n" +
            "WHERE v.id = :videoId"
    ),

    ;

    private final String sql;

    SqlPECVideoQuery(String sql) {
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
