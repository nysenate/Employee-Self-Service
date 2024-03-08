package gov.nysenate.ess.core.service.pec.task;



import gov.nysenate.ess.core.client.view.pec.video.PECCodeSubmission;
import gov.nysenate.ess.core.dao.pec.task.PersonnelTaskDao;
import gov.nysenate.ess.core.model.pec.ethics.DateRangedEthicsCode;
import gov.nysenate.ess.core.model.pec.ethics.IncorrectPECCodeAmountEx;
import gov.nysenate.ess.core.model.pec.ethics.IncorrectPECEthicsCodeEx;
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
    public PersonnelCodeVerificationService(PersonnelTaskDao personnelTaskDao){
        this.personnelTaskDao = personnelTaskDao;
    }

    public void verifyDateRangedEthics(PECCodeSubmission codeSubmission, String dateInput)
            throws IncorrectPECCodeAmountEx, IncorrectPECEthicsCodeEx {
        //History of codes
        List<DateRangedEthicsCode> codeList = personnelTaskDao.getEthicsCodes();
        Collections.sort(codeList);
        //error out if missing codes
        if (codeSubmission.getCodes().isEmpty() || codeSubmission.getCodes().size() < 2) {
            throw new IncorrectPECCodeAmountEx();
        }

        //User submitted codes
        String code1 = codeSubmission.getCodes().get(0).toUpperCase();
        String code2 = codeSubmission.getCodes().get(1).toUpperCase();
        LocalDateTime parsedDate = LocalDateTime.parse(dateInput.substring(0, 19));
        LocalDate codeDate = parsedDate.toLocalDate();
        //Submitted Task Id
        int submittedTaskId = codeSubmission.getTaskId();
        int matchedEntries = 0;

        //Attempt to match submitted task id to the ethics code id
        Integer confirmedEthicsCodeId = personnelTaskDao.getEthicsCodeId(submittedTaskId);

        //Cant confirm task
        if (confirmedEthicsCodeId == null) {
            throw new IncorrectPECEthicsCodeEx();
        }

        //Cycle thru the history of codes to see if 2 codes match and are within the correct date range
        for (DateRangedEthicsCode drec : codeList) {

            if (drec.getStartDate() == null || drec.getEndDate() == null) {
                continue;
            }

            LocalDate drecStartDate = drec.getStartDate().toLocalDate();
            LocalDate drecEndDate = drec.getEndDate().toLocalDate();

            if (drec.getEthicsCodeId() == confirmedEthicsCodeId && drec.getCode().equals(code1) && ( (drecStartDate.isBefore(codeDate) && drecEndDate.isAfter(codeDate))
                    || drecStartDate.equals(codeDate) || drecEndDate.equals(codeDate) ) ) {
                matchedEntries++;
            }
            else if (drec.getEthicsCodeId() == confirmedEthicsCodeId && drec.getCode().equals(code2) && ( (drecStartDate.isBefore(codeDate) && drecEndDate.isAfter(codeDate))
                    || drecStartDate.equals(codeDate) || drecEndDate.equals(codeDate) ) ) {
                matchedEntries++;
            }
        }

        //1 or more incorrect codes so error out
        if (matchedEntries < 2) {
            throw new IncorrectPECEthicsCodeEx();
        }

    }


}
