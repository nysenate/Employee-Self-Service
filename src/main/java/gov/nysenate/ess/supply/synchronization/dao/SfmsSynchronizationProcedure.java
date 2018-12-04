package gov.nysenate.ess.supply.synchronization.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import gov.nysenate.ess.core.config.CoreConfig;
import gov.nysenate.ess.core.config.JacksonConfig;
import gov.nysenate.ess.core.dao.base.SqlQueryUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.StoredProcedure;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an oracle procedure which records all item movements and updates inventory counts in the SFMS database.
 */
@Repository
public class SfmsSynchronizationProcedure extends StoredProcedure {

    private static final String RESPONSE = "response";
    private static final String PARAMETER = "requisitionXml";

    @Autowired
    public SfmsSynchronizationProcedure(ComboPooledDataSource remoteDataSource, @Qualifier("supplySyncProcedureName") String name) {
        super(remoteDataSource, name);
        declareParameter(new SqlOutParameter(RESPONSE, Types.NUMERIC));
        declareParameter(new SqlParameter(PARAMETER, Types.CHAR));
        setFunction(true);
        compile();
    }

    /**
     * Inserts a movement record into SFMS for each item in a requisition.
     * This keeps SFMS up to date with orders processed through the online supply web page.
     * <p>
     * Throws a DataAccessException if any errors occur while synchronizing the requisition.
     *
     * @param requisitionXml An XML representation of a requisition.
     *                       For expected xml format, use the {@link JacksonConfig#xmlObjectMapper()}
     *                       when serializing the requisition view.
     */
    public void synchronizeRequisition(String requisitionXml) {
        Map parameterMap = new HashMap();
        parameterMap.put(PARAMETER, requisitionXml);
        execute(parameterMap);
    }
}
