package gov.nysenate.ess.core.service.pec;

import gov.nysenate.ess.core.dao.pec.PersonnelAssignedTaskDao;
import gov.nysenate.ess.core.dao.personnel.EmployeeDao;
import gov.nysenate.ess.core.model.pec.PersonnelAssignedTask;
import gov.nysenate.ess.core.model.pec.PersonnelTaskId;
import gov.nysenate.ess.core.model.pec.PersonnelTaskType;
import gov.nysenate.ess.core.model.personnel.Employee;
import gov.nysenate.ess.core.model.personnel.EmployeeException;
import gov.nysenate.ess.core.service.personnel.EmployeeInfoService;
import gov.nysenate.ess.core.service.personnel.EmployeeSearchBuilder;
import gov.nysenate.ess.core.util.LimitOffset;
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
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class PECVideoCSVService {

    private static final Logger logger = LoggerFactory.getLogger(PECVideoCSVService.class);

    private EmployeeDao employeeDao;
    private PersonnelAssignedTaskDao personnelAssignedTaskDao;
    private EmployeeInfoService employeeInfoService;

    @Value("${data.dir}")
    private String dataDir;

    final private Pattern csvEthicsReportPattern =
            Pattern.compile("ExportDocsEthics(\\d{4})");

    final private Pattern csvHarrassmentReportPattern =
            Pattern.compile("ExportDocsHarassment(\\d{4})");

    final private Pattern csvLiveTrainingDHPReportPattern =
            Pattern.compile("LiveTrainingDHP(\\d{4})");

    final int ETHICS_VID_ID_NUM = 1;

    final int HARASSMENT_VID_ID_NUM = 2;


    @Autowired
    public PECVideoCSVService(EmployeeDao employeeDao, PersonnelAssignedTaskDao personnelAssignedTaskDao,
                              EmployeeInfoService employeeInfoService) {
        this.employeeDao = employeeDao;
        this.personnelAssignedTaskDao = personnelAssignedTaskDao;
        this.employeeInfoService = employeeInfoService;
    }

    public void processCSVReports() throws IOException {
        File[] files = getFilesFromDirectory(dataDir);

        for (File file : files) {
            String fileName = file.getName().replaceAll(".csv", "").replaceAll(".xlsx", "");
            Matcher ethicsMatcher = csvEthicsReportPattern.matcher(fileName);
            Matcher harassmentMatcher = csvHarrassmentReportPattern.matcher(fileName);
            Matcher liveTrainingDHPMatcher = csvLiveTrainingDHPReportPattern.matcher(fileName);

            if (ethicsMatcher.matches()) {
                logger.info("Processing Ethics CSV report");
                handleGeneratedReportCSV(file, ETHICS_VID_ID_NUM, 10); //IDS may change once we get the videos
            } else if (harassmentMatcher.matches()) {
                logger.info("Processing Harassment CSV report");
                handleGeneratedReportCSV(file, HARASSMENT_VID_ID_NUM, 11);
            } else if (liveTrainingDHPMatcher.matches()) {
                logger.info("Processing Live Training DHP CSV report");
                handleLiveTrainingReportCSV(file);
            }
        }
    }

    private void handleGeneratedReportCSV(File genRepCSV, int id, int dateColumnNumber) throws IOException {
        Reader reader = Files.newBufferedReader(Paths.get(genRepCSV.getAbsolutePath()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        for (CSVRecord csvRecord : csvParser) {
            // Accessing Values by Column Index
            //0 Name
            //1 EmpID
            //2 Office
            //3 Location
            //4 Phone
            //5 Email
            //6 Training
            //7 Watched Video
            //8 EC1
            //9 EC2
            //10 Date 02/21/2018 11:27:18 EST
            if (csvRecord.get(0).equals("Name")) {
                continue;
            }


            Integer fileEmpId = -1;
            try {
                fileEmpId = Integer.parseInt(csvRecord.get(1));

                String dateFromFile = csvRecord.get(dateColumnNumber)
                        .replaceAll("EST", "").replaceAll("EDT", "").trim();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
                LocalDateTime dateTime = LocalDateTime.parse(dateFromFile, formatter);

                Employee employee = employeeDao.getEmployeeById(fileEmpId);

                updateDB(employee, dateTime, id);
            }
            catch (EmployeeException e) {
                logger.warn("CSV report has EmpID " + fileEmpId +
                        " in record number: " + csvRecord.getRecordNumber() + " which could not be found by ESS");
            }
            catch (NumberFormatException e) {
                logger.warn("CSV report has problematic data in the EmpID field in record number: "
                        + csvRecord.getRecordNumber());
            }
        }
    }

    private void handleLiveTrainingReportCSV(File liveTrainingCSV) throws IOException {
        //this will handle the custom csv file when it comes in
        Reader reader = Files.newBufferedReader(Paths.get(liveTrainingCSV.getAbsolutePath()));
        CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL);
        int failedToMatchCount = 0;
        for (CSVRecord csvRecord : csvParser) {
            //0 EMPLOYEE NAME
            //1 OFFICE
            //2 TRAINING DATE
            if (csvRecord.get(0).equals("EMPLOYEE NAME")) {
                continue;
            }
            String nameFromFile = csvRecord.get(0);
            String[] splitName = nameFromFile.split(","); //Lastname is slot 0 and firstname in slot 1
            String printName = splitName[1] + " " + splitName[0];
            String fileDate = csvRecord.get(2);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("M/dd/yyyy HH:mm");
            DateTimeFormatter dateTimeFormatter2 = DateTimeFormatter.ofPattern("M/d/yyyy HH:mm");
            LocalDateTime dateTime = null;

            try {
                dateTime = LocalDateTime.parse(fileDate, dateTimeFormatter);
            }
            catch (DateTimeParseException e) {
                //This probably means the date is a single digit day. Throw exceptions from here
                dateTime = LocalDateTime.parse(fileDate, dateTimeFormatter2);
            }

            EmployeeSearchBuilder employeeSearchBuilder = new EmployeeSearchBuilder();
            employeeSearchBuilder.setName(nameFromFile.replaceAll("," , ""));
            List<Employee> potentialEmployees = employeeInfoService.searchEmployees(employeeSearchBuilder, LimitOffset.ALL).getResults();

            if (potentialEmployees.size() == 1) {
                //Singular employee, insert the update to the db
                updateDB(potentialEmployees.get(0), dateTime, HARASSMENT_VID_ID_NUM);
            }
            else if (potentialEmployees.size() == 0) {
                //WARN of an employee that does not exist / could not be found
                logger.warn("NO employees were found for the name "  + printName
                        + " in row " + csvRecord.getRecordNumber());
                failedToMatchCount++;

            }
            else {
                //Multiple potential employees were found.
                logger.warn("Multiple employees were found for the name " + printName
                        + " in row " + csvRecord.getRecordNumber());
                failedToMatchCount++;
            }
        }
        logger.warn(failedToMatchCount + " records failed to match");
    }

    private void updateDB(Employee employee, LocalDateTime dateTime, int id) {
        PersonnelAssignedTask taskToInsert = new PersonnelAssignedTask(
                employee.getEmployeeId(),
                new PersonnelTaskId(PersonnelTaskType.VIDEO_CODE_ENTRY, id), //ID of the video
                dateTime,
                employee.getEmployeeId(),
                true
        );
        personnelAssignedTaskDao.updatePersonnelAssignedTask(taskToInsert);
    }

    private File[] getFilesFromDirectory(String path) {
        return new File(dataDir).listFiles();
    }

    private boolean checkIfFileExists(String filename) {
        File file = new File(dataDir + filename);
        return file.exists();
    }

}
