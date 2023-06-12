package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.application.AmendmentView;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.amendment.Amendment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DraftService {

    private static final Logger logger = LoggerFactory.getLogger(DraftService.class);

    private DraftDao draftDao;
    private TravelEmployeeService travelEmployeeService;

    @Autowired
    public DraftService(DraftDao draftDao, TravelEmployeeService travelEmployeeService) {
        this.draftDao = draftDao;
        this.travelEmployeeService = travelEmployeeService;
    }

    public List<Draft> getUserDrafts(int userId) {
        List<DraftRecord> records = draftDao.find(userId);
        return records.stream()
                .map(this::createDraft)
                .collect(Collectors.toList());
    }

    public Draft saveDraft(Draft draft) {
        DraftRecord record = new DraftRecord(draft);
        record = draftDao.save(record);
        return createDraft(record);
    }

    private Draft createDraft(DraftRecord record) {
        TravelEmployee travelEmployee = travelEmployeeService.getTravelEmployee(record.travelerEmpId);
        Amendment amd;
        try {
            amd = deserializeAmendment(record.amendmentJson);
        } catch (IOException ex) {
            logger.error("Unable to deserialize amendment for Draft with id: " + record.id);
            amd = new Amendment.Builder().build();
        }
        return new Draft(record.id, record.userEmpId, travelEmployee, amd, record.updatedDateTime);
    }

    private String serializeAmendment(Amendment amd) {
        return OutputUtils.toJson(new AmendmentView(amd));
    }

    private Amendment deserializeAmendment(String json) throws IOException {
        return OutputUtils.jsonToObject(json, AmendmentView.class).toAmendment();
    }
}
