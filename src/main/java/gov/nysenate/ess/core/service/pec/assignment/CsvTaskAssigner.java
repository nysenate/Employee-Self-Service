package gov.nysenate.ess.core.service.pec.assignment;

import gov.nysenate.ess.core.dao.pec.assignment.PersonnelTaskAssignmentDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelTaskAssignment;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeNotFoundEx;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CsvTaskAssigner {

    private static final Logger logger = LoggerFactory.getLogger(CsvTaskAssigner.class);

    private final EmployeeDao employeeDao;
    private final PersonnelTaskAssignmentDao personnelTaskAssignmentDao;
    private final EmployeeInfoService employeeInfoService;

    private final String csvFileDir;

    private static final Pattern manualAsssignmentCSV =
            Pattern.compile("manual_assignment_V(\\d)");


    @Autowired
    public CsvTaskAssigner(EmployeeDao employeeDao,
                              PersonnelTaskAssignmentDao personnelTaskAssignmentDao,
                              EmployeeInfoService employeeInfoService,
                              @Value("${data.dir:}") String dataDir) {
        this.employeeDao = employeeDao;
        this.personnelTaskAssignmentDao = personnelTaskAssignmentDao;
        this.employeeInfoService = employeeInfoService;
        this.csvFileDir = Paths.get(dataDir, "pec_csv_import").toString();
    }

    public void processCSVForManualAssignments() throws IOException {
        File[] files = getFilesFromDirectory(csvFileDir);

        for (File file : files) {
            String fileName = file.getName().replaceAll(".csv", "").replaceAll(".xlsx", "");
            Matcher manualMatcher = manualAsssignmentCSV.matcher(fileName);

            if (manualMatcher.matches()) {
                processManualAssignments(file);
            }

        }
    }

    //This method assigns and unassigns tasks only
    private void processManualAssignments (File manualAssignmentCSV) throws IOException {
        //empID, taskID, active, updateEmpID

        Reader reader = Files.newBufferedReader(Paths.get(manualAssignmentCSV.getAbsolutePath()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        for (CSVRecord csvRecord : csvParser) {

            if (csvRecord.get(0).equals("empID")) {
                continue;
            }

            try {
                int empId = Integer.parseInt(csvRecord.get(0));
                int taskId = Integer.parseInt(csvRecord.get(1));
                boolean active = Boolean.parseBoolean(csvRecord.get(2));
                int updateEmpID = Integer.parseInt(csvRecord.get(3));

                //verify that the employee exists. We don't want bad data in the database
                Employee employee = employeeDao.getEmployeeById(empId);


                if (!active) {
                    personnelTaskAssignmentDao.deactivatePersonnelTaskAssignment(empId, taskId);
                }
                else {
                    logger.info("Creating task for emp " + empId + " for task " + taskId);
                    PersonnelTaskAssignment taskToInsertForEmp =
                            new PersonnelTaskAssignment(
                                    taskId,empId,updateEmpID, LocalDateTime.now(),false, active);

                    personnelTaskAssignmentDao.updateAssignment(taskToInsertForEmp);
                }

            }
            catch (EmployeeNotFoundEx e) {
                logger.warn("Could not find employee in ESS.  empId: {}\trecord: {}", e.getEmpId(), csvRecord);
            }

        }

    }


    private File[] getFilesFromDirectory(String path) {
        return new File(path).listFiles();
    }
}
