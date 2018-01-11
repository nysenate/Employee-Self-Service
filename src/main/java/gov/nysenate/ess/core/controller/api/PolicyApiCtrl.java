package gov.nysenate.ess.core.controller.api;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import gov.nysenate.ess.core.client.response.base.ListViewResponse;
import gov.nysenate.ess.core.client.response.base.SimpleResponse;
import gov.nysenate.ess.core.client.view.policy.AcknowledgementView;
import gov.nysenate.ess.core.client.view.policy.PolicyView;
import gov.nysenate.ess.core.model.base.InvalidRequestParamEx;
import gov.nysenate.ess.core.model.policy.Acknowledgement;
import gov.nysenate.ess.core.model.policy.Policy;
import gov.nysenate.ess.core.util.ShiroUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/policies")
public class PolicyApiCtrl extends BaseRestApiCtrl {

    private static final ImmutableList<Policy> dummyPolicies = ImmutableList.<Policy>builder()
            .add(new Policy("Policy 1", "dummy.pdf", true, 1, LocalDateTime.now()))
            .add(new Policy("Policy 2", "dummy.pdf", true, 2, LocalDateTime.now()))
            .add(new Policy("Policy 3", "dummy.pdf", true, 3, LocalDateTime.now()))
            .add(new Policy("Policy 4", "dummy.pdf", true, 4, LocalDateTime.now()))
            .build();

    private static final ImmutableSet<Integer> dummyPolicyIds = dummyPolicies.stream()
            .map(Policy::getPolicyId)
            .collect(collectingAndThen(toSet(), ImmutableSet::copyOf));

    @RequestMapping(value = "", method = {GET, HEAD})
    public ListViewResponse<PolicyView> getActivePolicies(HttpServletRequest request) {

        String ctxPath = request.getContextPath();

        List<PolicyView> policyViews = dummyPolicies.stream()
                .map(policy -> new PolicyView(policy, ctxPath))
                .collect(toList());

        return ListViewResponse.of(policyViews, "policies");
    }

    @RequestMapping(value = "/acknowledgements", method = {GET, HEAD})
    public ListViewResponse<AcknowledgementView> getPolicyAcknowledgements(@RequestParam int empId) {
        List<Acknowledgement> acknowledgements = ImmutableList.<Acknowledgement>builder()
                .add(new Acknowledgement(empId, 1, LocalDateTime.now()))
                .add(new Acknowledgement(empId, 2, LocalDateTime.now()))
                .build();

        List<AcknowledgementView> ackViews = acknowledgements.stream()
                .map(AcknowledgementView::new)
                .collect(toList());

        return ListViewResponse.of(ackViews, "acknowledgements");
    }

    @RequestMapping(value = "/acknowledgements", method = POST)
    public SimpleResponse acknowledgePolicy(@RequestParam int empId,
                                            @RequestParam int policyId) {

        // TODO replace these verifications with security checks and better validation
        if (ShiroUtils.getAuthenticatedEmpId() != empId) {
            throw new InvalidRequestParamEx(String.valueOf(empId), "empId", "int",
                    "testtttttttttt");
        }
        if (!dummyPolicyIds.contains(policyId)) {
            throw new InvalidRequestParamEx(String.valueOf(policyId), "policyId", "int",
                    "testtttttttttt");
        }

        return new SimpleResponse(true, "Policy Acknowledged", "policy-acknowledged");
    }

}
