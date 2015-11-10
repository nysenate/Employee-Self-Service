package gov.nysenate.ess.core.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@Profile({"test"})
@Transactional
@TransactionConfiguration(transactionManager = "remoteTxManager", defaultRollback = true)
@Import({LdapConfig.class, DbConnectionPoolConfig.class, DatabaseConfig.class})
@ComponentScan("gov.nysenate.ess.core.dao")
public class DaoConfig {}
