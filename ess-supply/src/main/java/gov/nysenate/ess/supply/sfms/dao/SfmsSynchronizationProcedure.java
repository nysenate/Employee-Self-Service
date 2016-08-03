package gov.nysenate.ess.supply.sfms.dao;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import gov.nysenate.ess.core.config.CoreConfig;
import gov.nysenate.ess.supply.error.SupplyErrorLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
 * Wraps an oracle procedure which synchronizes supply data with sfms data.
 */
@Repository
public class SfmsSynchronizationProcedure extends StoredProcedure {

    private static final String PROCEDURE_NAME = "SYNCHRONIZE_SUPPLY.synchronize_with_supply";
    private static final String RESPONSE = "response";
    private static final String PARAMETER = "requisitionXml";

    private static final Logger logger = LoggerFactory.getLogger(SfmsSynchronizationProcedure.class);
    @Autowired private SupplyErrorLogService errorLogService;

    @Autowired
    public SfmsSynchronizationProcedure(ComboPooledDataSource remoteDataSource) {
        super(remoteDataSource, PROCEDURE_NAME);
        declareParameter(new SqlOutParameter(RESPONSE, Types.NUMERIC));
        declareParameter(new SqlParameter(PARAMETER, Types.CHAR));
        setFunction(true);
        compile();
    }

    /**
     * Inserts a movement record into SFMS for each item in a requisition.
     * This keeps SFMS up to date with orders processed through the online supply web page.
     * @param requisitionXml An XML representation of a requisition.
     *                       For expected xml format, use the {@link CoreConfig#xmlObjectMapper()}
     *                       when serializing the requisition view.
     * @return The requisition id if successful, 0 otherwise.
     */
    public int synchronizeRequisition(String requisitionXml) {
        Map parameterMap = new HashMap();
        parameterMap.put(PARAMETER, requisitionXml);
        try {
            Map responseMap = execute(parameterMap);
            if (!responseMap.isEmpty()) {
                return ((BigDecimal) responseMap.get(RESPONSE)).intValue();
            }
        } catch (UncategorizedSQLException ex) {
            String message = "Error synchronizing with SFMS. Exception is : " + ex.getMessage();
            logger.error(message);
            errorLogService.saveError(message);
        }
        return 0;
    }
}
