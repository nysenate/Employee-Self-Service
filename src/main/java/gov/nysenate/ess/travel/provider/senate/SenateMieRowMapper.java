package gov.nysenate.ess.travel.provider.senate;

import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class SenateMieRowMapper implements RowMapper<SenateMie> {

    @Override
    public SenateMie mapRow(ResultSet rs, int i) throws SQLException {
        return new SenateMie(
                rs.getInt("senate_mie_id"),
                rs.getInt("fiscal_year"),
                new Dollars(rs.getString("total")),
                new Dollars(rs.getString("breakfast")),
                new Dollars(rs.getString("dinner"))
        );
    }
}