package gov.nysenate.ess.core.dao.base;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SqlBaseDao
{
    @Resource(name = "localJdbcTemplate")
    protected JdbcTemplate localJdbc;

    @Resource(name = "localNamedJdbcTemplate")
    protected NamedParameterJdbcTemplate localNamedJdbc;

    @Resource(name = "remoteJdbcTemplate")
    protected JdbcTemplate remoteJdbc;

    @Resource(name = "remoteNamedJdbcTemplate")
    protected NamedParameterJdbcTemplate remoteNamedJdbc;

    /** The main production schema. Intended for read access only. */
    @Value("${master.schema}")
    protected String MASTER_SCHEMA;

    /** The time/attendance buffer schema. Permits writes. */
    @Value("${ts.schema}")
    protected String TS_SCHEMA;

    /** The schema for the Supply app. */
    @Value("${supply.schema}")
    protected String SUPPLY_SCHEMA;

    private static Map<String, String> schemaMap = null;

    /**
     * Returns a string substitution map for setting the configured schema names.
     * @return Map<String, String>
     */
    protected Map<String, String> schemaMap() {
        if (schemaMap == null) {
            schemaMap = new HashMap<>();
            schemaMap.put("masterSchema", MASTER_SCHEMA);
            schemaMap.put("tsSchema", TS_SCHEMA);
            schemaMap.put("supplySchema", SUPPLY_SCHEMA);
        }
        return schemaMap;
    }

    /**
     * Convert a LocalDate to a Date. Returns null on null input.
     */
    public static java.sql.Date toDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }

    /**
     * Convert a LocalDateTime to a Date. Returns null on null input.
     */
    public static Timestamp toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Timestamp.valueOf(localDateTime);
    }

    public static Time toTime(LocalTime localTime) {
        if (localTime == null) return null;
        return Time.valueOf(localTime);
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDateTime.
     */
    public static LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        if (rs.getTimestamp(column) == null) return null;
        return rs.getTimestamp(column).toLocalDateTime();
    }

    /**
     * Read the 'column' date value from the result set and cast it to a LocalDate.
     */
    public static LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        if (rs.getDate(column) == null) return null;
        return rs.getDate(column).toLocalDate();
    }

    /**
     * Converts true to 'A' and false to 'I'
     * @param status Boolean
     * @return char
     */
    public static char getStatusCode(Boolean status) {
        return (status != null && status.equals(true)) ? 'A' : 'I';
    }
}