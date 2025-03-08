package gov.nysenate.ess.core.service.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PotentialEmployeeRejectionService {

    private static final Logger logger = LoggerFactory.getLogger(PotentialEmployeeRejectionService.class);
    private final String techInternEmail;
    private final String dateDir;
    private final String internSubdir;
    private final String internRejectionTitlePartOne = "NYS STS ";
    private final String internRejectionTitlePartTwo = " Internship";
    private final String internRejectionBodyPartOne = "Hello ";
    private final String internRejectionBodyPartTwo = "\n\n" +
            "Unfortunately, we will not be advancing you in our hiring process for our ";
    private final String internRejectionBodyPartThree =
            " internship.\nThank you for taking the time to apply to our internship! Best of luck finding your next position!";
    private final String internRejectionConclusion = "\n\nThanks again!";
    private final MimeSendMailService mimeSendMailService;


    @Autowired
    public PotentialEmployeeRejectionService(MimeSendMailService mimeSendMailService,
                                             @Value("${internship.email}") String techInternEmail,
                                             @Value("${data.dir}") String dataDir,
                                             @Value("${data.intern_subdir}") String internSubDir) {
        this.mimeSendMailService = mimeSendMailService;
        this.techInternEmail = techInternEmail;
        this.dateDir = dataDir;
        this.internSubdir = internSubDir;
    }

    public void ProcessInternEmails(int year, InternshipSeason internshipSeason) throws FileNotFoundException {
        ensureDirectory();

        String fileName = this.dateDir + this.internSubdir + internshipSeason.getSeason().toUpperCase() + "-" + year + ".csv";
        logger.info("Processing " + fileName);

        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            List<List<String>> records = lines.map(line -> Arrays.asList(line.split(",")))
                    .collect(Collectors.toList());

            for (List<String> record : records) { //First line of the CSV should be the email
                SendInternRejectionEmail(year, internshipSeason, record.get(0), record.get(1));
            }
        } catch (Exception e) {
            String errorMessage = "Missing File: " + internshipSeason.getSeason().toUpperCase() + "-" + year + ".csv";
            logger.error(errorMessage);
            logger.error(e.getMessage());
            throw new FileNotFoundException(errorMessage);
        }
    }

    private void ensureDirectory() {
        String directoryPath = this.dateDir + this.internSubdir;
        File directory = new File(directoryPath);

        if (directory.exists()) {
        } else {
            directory.mkdirs();
        }
    }

    private boolean checkFileName(int year, InternshipSeason internshipSeason) {
        return new File(this.dateDir + this.internSubdir + internshipSeason.getSeason() + "-" + year + ".csv").exists();
    }

    private void SendInternRejectionEmail(int year, InternshipSeason internshipSeason, String email, String name) {

        String title = internRejectionTitlePartOne + internshipSeason.getSeason() + " " + year
                + internRejectionTitlePartTwo;

        String html = internRejectionBodyPartOne + name + "," + internRejectionBodyPartTwo
                + internshipSeason.getSeason() + " " + year
                + internRejectionBodyPartThree + internRejectionConclusion;

        mimeSendMailService.sendMessage(email, this.techInternEmail, title, html);

    }

}
