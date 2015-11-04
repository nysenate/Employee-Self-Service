package gov.nysenate.ess.web.config;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Configures dependencies necessary for any database related operations.
 */
@EnableTransactionManagement
@Configuration
public class DatabaseConfig
{
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConfig.class);

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

    @Bean(name = "localTxManager")
    public PlatformTransactionManager localTxManager() {
        return new DataSourceTransactionManager(localDataSource);
    }

    @Bean(name = "remoteTxManager")
    public PlatformTransactionManager remoteTxManager() {
        return new DataSourceTransactionManager(remoteDataSource);
    }
}