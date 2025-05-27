package gov.nysenate.ess.core.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.beans.PropertyVetoException;

@Configuration
public class DbConnectionPoolConfig
{
    private static final Logger logger = LoggerFactory.getLogger(DbConnectionPoolConfig.class);

    /** Local Database Configuration */
    @Value("${db.local.driver}") private String dbLocalDriver;
    @Value("${db.local.type}") private String dbLocalType;
    @Value("${db.local.host}") private String dbLocalHost;
    @Value("${db.local.name}") private String dbLocalName;
    @Value("${db.local.user}") private String dbLocalUser;
    @Value("${db.local.pass}") private String dbLocalPass;

    /** Remote Database Configuration */
    @Value("${db.remote.driver}") private String dbRemoteDriver;
    @Value("${db.remote.type}") private String dbRemoteType;
    @Value("${db.remote.host}") private String dbRemoteHost;
    @Value("${db.remote.name}") private String dbRemoteName;
    @Value("${db.remote.user}") private String dbRemoteUser;
    @Value("${db.remote.pass}") private String dbRemotePass;

    /**
     * Configures and returns the local data source.
     * @return ComboPooledDataSource
     */
    @Bean(destroyMethod = "close", name = "localDataSource")
    public ESSComboPooledDataSource localDataSource() {
        ESSComboPooledDataSource cpds = getComboPooledDataSource(dbLocalType, dbLocalHost, dbLocalName, dbLocalDriver,
                dbLocalUser, dbLocalPass);
        cpds.setMinPoolSize(3);
        cpds.setMaxPoolSize(10);

        /** Verify connectivity before handing over the connection. */
        cpds.setTestConnectionOnCheckout(true);
        cpds.setPreferredTestQuery("SELECT 1");

        return cpds;
    }

    /**
     * Configures and returns the remote data source.
     * @return ComboPooledDataSource
     */
    @Bean(destroyMethod = "close", name = "remoteDataSource")
    public ESSComboPooledDataSource remoteDataSource() {
        ESSComboPooledDataSource cpds = getComboPooledDataSource(dbRemoteType, dbRemoteHost, dbRemoteName, dbRemoteDriver,
                dbRemoteUser, dbRemotePass);
        cpds.setMinPoolSize(3);
        cpds.setMaxPoolSize(10);

        /** Refresh the pool every 3 hours */
        cpds.setMaxIdleTime(10800);

        /** Verify connectivity before handing over the connection. */
        cpds.setTestConnectionOnCheckout(true);
        cpds.setPreferredTestQuery("SELECT 1 FROM DUAL");

        /** Set max connection retry attempts */
        cpds.setAcquireRetryAttempts(2);
        return cpds;
    }

    /**
     * Creates a basic pooled DataSource.
     *
     * @param type Database type
     * @param host Database host address
     * @param name Database name
     * @param driver Database driver string
     * @param user Database user
     * @param pass Database password
     * @return PoolProperties
     */
    private ESSComboPooledDataSource getComboPooledDataSource(String type, String host, String name, String driver,
                                                           String user, String pass) {
        final String jdbcUrlTemplate = "jdbc:%s//%s/%s";
        var pool = new ESSComboPooledDataSource();
        try {
            pool.setDriverClass(driver);
        }
        catch (PropertyVetoException ex) {
            logger.error("Error when setting the database driver {}{}", driver, ex.getMessage());
        }
        final String jdbcUrl = String.format(jdbcUrlTemplate, type, host, name);

        pool.setJdbcUrl(jdbcUrl);
        pool.setUser(user);
        pool.setPassword(pass);
        return pool;
    }
}
