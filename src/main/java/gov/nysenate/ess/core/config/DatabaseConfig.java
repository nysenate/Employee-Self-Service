package gov.nysenate.ess.core.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import gov.nysenate.ess.core.dao.base.SqlQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.HashMap;
import java.util.Map;

/**
 * Configures dependencies necessary for any database related operations.
 */
@EnableTransactionManagement
@Configuration
public class DatabaseConfig {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

    public static final String localTxManager = "localTxManager";
    public static final String remoteTxManager = "remoteTxManager";

    @Autowired ComboPooledDataSource localDataSource;
    @Autowired ComboPooledDataSource remoteDataSource;

    @Bean(name = "localJdbcTemplate")
    public JdbcTemplate localJdbcTemplate() {
        return new JdbcTemplate(localDataSource);
    }

    @Bean(name = "localNamedJdbcTemplate")
    public NamedParameterJdbcTemplate localNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(localDataSource);
    }

    @Bean(name = "remoteJdbcTemplate")
    public JdbcTemplate remoteJdbcTemplate() {
        return new JdbcTemplate(remoteDataSource);
    }

    @Bean(name = "remoteNamedJdbcTemplate")
    public NamedParameterJdbcTemplate remoteNamedJdbcTemplate() {
        return new NamedParameterJdbcTemplate(remoteDataSource);
    }

    @Bean(name = localTxManager)
    public PlatformTransactionManager localTxManager() {
        return new DataSourceTransactionManager(localDataSource);
    }

    @Bean(name = "remoteTxManager")
    public PlatformTransactionManager remoteTxManager() {
        return new DataSourceTransactionManager(remoteDataSource);
    }

    /**
     * Configures the string substitution map for setting the configured schema names.
     * @return Map<String, String>
     */
    @Bean(name = "schemaMap")
    public Map<String, String> schemaMap() {
        Map<String, String> schemaMap = new HashMap<>();
        schemaMap.put("masterSchema", MASTER_SCHEMA);
        schemaMap.put("tsSchema", TS_SCHEMA);
        schemaMap.put("essSchema", ESS_SCHEMA);
        schemaMap.put("supplySchema", SUPPLY_SCHEMA);
        return schemaMap;
    }

    /** The main production schema. Intended for read access only. */
    @Value("${master.schema}")
    protected String MASTER_SCHEMA;

    /** The time/attendance buffer schema. Permits writes. */
    @Value("${ts.schema}")
    protected String TS_SCHEMA;

    /** The shared Ess schema */
    @Value("${ess.schema}")
    protected String ESS_SCHEMA;

    /** The schema for the Supply app. */
    @Value("${supply.schema}")
    protected String SUPPLY_SCHEMA;

    /** Configures the supply sync procedure name prefixed with the correct schema. */
    @Bean(name = "supplySyncProcedureName")
    public String supplySyncProcedureName(@Qualifier("schemaMap") Map<String, String> schemaMap) {
        String name = "${masterSchema}.SYNCHRONIZE_SUPPLY.synchronize_with_supply";
        return SqlQueryUtils.substituteSchema(schemaMap, name);
    }
}
