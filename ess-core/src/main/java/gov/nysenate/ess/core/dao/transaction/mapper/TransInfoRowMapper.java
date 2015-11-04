package gov.nysenate.ess.core.dao.transaction.mapper;

import gov.nysenate.ess.core.model.transaction.TransactionCode;
import gov.nysenate.ess.core.model.transaction.TransactionInfo;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import static gov.nysenate.ess.core.dao.base.BaseMapper.getLocalDateFromRs;
import static gov.nysenate.ess.core.dao.base.BaseMapper.getLocalDateTimeFromRs;

public class TransInfoRowMapper implements RowMapper<TransactionInfo> {

    protected String pfx;

    public TransInfoRowMapper(String pfx) {
        this.pfx = pfx;
    }

    public TransInfoRowMapper() {
        this("");
    }

    @Override
    public TransactionInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        TransactionInfo transInfo = new TransactionInfo();
        transInfo.setTransCode(TransactionCode.valueOf(rs.getString(pfx + "CDTRANS")));
        transInfo.setEmployeeId(rs.getInt(pfx + "NUXREFEM"));
        transInfo.setActive(rs.getString(pfx + "CDSTATUS").equals("A"));
        transInfo.setChangeId(rs.getInt(pfx + "NUCHANGE"));
        transInfo.setDocumentId(rs.getString(pfx + "NUDOCUMENT"));
        transInfo.setOriginalDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNORIGIN"));
        transInfo.setUpdateDate(getLocalDateTimeFromRs(rs, pfx + "DTTXNUPDATE"));
        transInfo.setEffectDate(getLocalDateFromRs(rs, pfx + "DTEFFECT"));
        return transInfo;
    }
}
