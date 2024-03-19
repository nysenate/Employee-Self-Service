package gov.nysenate.ess.core.service.pec.task;

import gov.nysenate.ess.core.client.view.pec.video.PECCodeSubmission;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.IncorrectCodeException;
import gov.nysenate.ess.core.model.pec.ethics.DateRangedEthicsCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class PersonnelCodeVerificationService {

    private final PersonnelTaskDao personnelTaskDao;
    @Autowired
    public PersonnelCodeVerificationService(PersonnelTaskDao personnelTaskDao) {
        this.personnelTaskDao = personnelTaskDao;
    }

    public void verifyDateRangedEthics(PECCodeSubmission codeSubmission) throws IncorrectCodeException {
        //History of codes
        List<DateRangedEthicsCode> codeList = personnelTaskDao.getEthicsCodes();
        Collections.sort(codeList);

        //error out if missing codes
        if (codeSubmission.getCodes().isEmpty() || codeSubmission.getCodes().size() < 2) {
            throw new IncorrectCodeException("A minimum of two codes must be submitted");
        }

        //User submitted codes and training date (converted to Epoch days)
        String code1 = codeSubmission.getCodes().get(0).toUpperCase();
        String code2 = codeSubmission.getCodes().get(1).toUpperCase();
        long submitEpochDays = LocalDateTime.parse(codeSubmission.getTrainingDate().substring(0, 19)).toLocalDate().toEpochDay();
        int matchedEntries = 0;

        //Attempt to match submitted task id to the ethics code id
        Integer confirmedEthicsCodeId = personnelTaskDao.getEthicsCodeId(codeSubmission.getTaskId());

        // Unable to reference the EthicsCodeId using the TaskId
        if (confirmedEthicsCodeId == null) {
            throw new IncorrectCodeException("Unable to reference the Ethics Code ID for this task");
        }

        //Cycle thru the history of codes to see if 2 codes that are associated
        //with the submitted task id match and are within the correct date range
        for (DateRangedEthicsCode drec : codeList) {
            if (drec.getStartDate() == null || drec.getEndDate() == null) {
                continue;
            }

            String drecCode = drec.getCode();
            Integer drecEthicsCodeId = drec.getEthicsCodeId();
            long startEpochDays = drec.getStartDate().toLocalDate().toEpochDay();
            long endEpochDays = drec.getEndDate().toLocalDate().toEpochDay();

            if (drecEthicsCodeId == confirmedEthicsCodeId && submitEpochDays >= startEpochDays && submitEpochDays <= endEpochDays && (drecCode.equals(code1) || drecCode.equals(code2))) {
                matchedEntries++;
            }
        }

        // Must match both codes.  If not, then throw an error.
        if (matchedEntries < 2) {
            throw new IncorrectCodeException();
        }

    }

}
