package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;
import gov.nysenate.ess.core.model.pec.PersonnelTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.pec.acknowledgment.AckDoc;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AckDocRowMapper extends BaseRowMapper<AckDoc> {

    private String pfx = "";

    public AckDocRowMapper(String pfx) {
        this.pfx = pfx;
    }

    @Override
    public AckDoc mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new AckDoc(
                new PersonnelTask(
                        rs.getInt(pfx + "id"),
                        PersonnelTaskType.DOCUMENT_ACKNOWLEDGMENT,
                        rs.getString(pfx + "title"),
                        getLocalDateTimeFromRs(rs, pfx + "effective_date_time"),
                        getLocalDateTimeFromRs(rs, pfx + "end_date_time"),
                        rs.getBoolean(pfx + "active")),
                rs.getString(pfx + "filename"));
    }
}
