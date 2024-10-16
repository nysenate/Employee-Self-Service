package gov.nysenate.ess.travel.request.draft;

import gov.nysenate.ess.core.util.OutputUtils;
import gov.nysenate.ess.travel.api.application.TravelApplicationView;
import gov.nysenate.ess.travel.department.DepartmentNotFoundEx;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.employee.TravelEmployeeService;
import gov.nysenate.ess.travel.request.app.TravelApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /**
     * @param draftId    The id of the draft to fetch.
     * @param employeeId The employee id of the logged-in user.
     */
    public Draft getDraft(int draftId, int employeeId) throws DepartmentNotFoundEx {
        DraftRecord record = draftDao.find(draftId, employeeId);
        return createDraft(record);
    }

    /**
     * Delete a draft.
     *
     * @param draftId    The id of the draft to delete.
     * @param employeeId The employee id of the logged-in user.
     */
    public void deleteDraft(int draftId, int employeeId) {
        draftDao.delete(draftId, employeeId);
    }

    public List<Draft> getUserDrafts(int userId) throws DepartmentNotFoundEx {
        List<Draft> userDrafts = new ArrayList<>();
        List<DraftRecord> records = draftDao.draftsForEmpId(userId);
        for (DraftRecord record : records) {
            userDrafts.add(createDraft(record));
        }
        return userDrafts;
    }

    public Draft saveDraft(Draft draft) throws DepartmentNotFoundEx {
        draft.setUpdatedDateTime(LocalDateTime.now());
        DraftRecord record = new DraftRecord(draft);
        record = draftDao.save(record);
        return createDraft(record);
    }

    private Draft createDraft(DraftRecord record) throws DepartmentNotFoundEx {
        TravelEmployee travelEmployee = travelEmployeeService.loadTravelEmployee(record.travelerEmpId);
        TravelApplication app;
        try {
            app = OutputUtils.jsonToObject(record.travelAppJson, TravelApplicationView.class).toTravelApplication();
        } catch (IOException ex) {
            logger.error("Unable to deserialize amendment for Draft with id: " + record.id);
            app = new TravelApplication.Builder(travelEmployee, travelEmployee.getDeptHeadId()).build();
        }
        Draft d = new Draft(record.userEmpId, travelEmployee);
        d.setId(record.id);
        d.setTravelApplication(app);
        d.setUpdatedDateTime(record.updatedDateTime);
        return d;
    }
}
