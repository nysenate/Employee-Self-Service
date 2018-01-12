package gov.nysenate.ess.core.dao.acknowledgement;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.acknowledgement.AckDoc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AckDocRowMapper extends BaseRowMapper<AckDoc> {

    private String pfx = "";

    public AckDocRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public AckDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
        AckDoc ackDoc = new AckDoc();
        ackDoc.setId(rs.getInt(pfx +"id"));
        ackDoc.setTitle(rs.getString(pfx + "title"));
        ackDoc.setFilename(rs.getString(pfx + "filename"));
        ackDoc.setActive(rs.getBoolean(pfx + "active"));
        ackDoc.setEffectiveDateTime(getLocalDateTimeFromRs(rs,"effective_date_time"));
        return ackDoc;
    }
}
