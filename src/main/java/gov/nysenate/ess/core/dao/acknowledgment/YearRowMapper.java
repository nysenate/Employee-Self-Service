package gov.nysenate.ess.core.dao.acknowledgment;

import gov.nysenate.ess.core.dao.base.BaseRowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class YearRowMapper extends BaseRowMapper<Integer> {

private String pfx="";

    public YearRowMapper(String pfx){
        this.pfx=pfx;
    }

    @Override
    public Integer mapRow(ResultSet rs, int rowNum)throws SQLException {
        return rs.getInt(pfx + "date_part");
    }
}

