package gov.nysenate.ess.core.dao.policy;

import gov.nysenate.ess.core.dao.base.SqlBaseDao;
import gov.nysenate.ess.core.dao.personnel.mapper.EmployeeRowMapper;
import gov.nysenate.ess.core.dao.policy.SqlPolicyQuery;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.model.policy.Acknowledgement;
import gov.nysenate.ess.core.model.policy.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class PolicyDao extends SqlBaseDao {

    private static final Logger logger = LoggerFactory.getLogger(PolicyDao.class);


    public Policy getPolicyById(int policyid) {
        Policy policy;
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("policyId",policyid);
        try {
            policy = localNamedJdbc.queryForObject(SqlPolicyQuery.GET_POLICY_BY_ID_SQL.getSql(schemaMap()), params, getPolicyRowMapper());
        }
        catch (DataRetrievalFailureException ex) {
            throw new EmployeeNotFoundEx("No matching policy record for policyid: " + policyid);
        }
        return policy;
    }

    public void insertPolicy(Policy policy) {

    }

    public List<Policy> getAllActivePolcies() {
        return new ArrayList<Policy>();
    }



    public Acknowledgement getAcknowledgementById(int empId, int policyId) {
        return new Acknowledgement();
    }

    public void insertAcknowledgement(Acknowledgement acknowledgement) {

    }

    public List<Acknowledgement> getAllAcknowledgements() {
        return new ArrayList<Acknowledgement>();
    }



    /** Returns a PolicyRowMapper that's configured for use in this dao */
    private static PolicyRowMapper getPolicyRowMapper() {
        return new PolicyRowMapper("");
    }

    /** Returns a EmployeeRowMapper that's configured for use in this dao */
    private static AcknowledgementRowMapper getAcknowledgementRowMapper() {
        return new AcknowledgementRowMapper("");
    }

}
