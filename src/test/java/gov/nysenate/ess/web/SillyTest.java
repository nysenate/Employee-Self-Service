package gov.nysenate.ess.web;

import gov.nysenate.ess.core.BaseTest;
import gov.nysenate.ess.travel.application.TravelApplication;
import gov.nysenate.ess.travel.application.TravelApplicationDao;
import gov.nysenate.ess.travel.report.pdf.TravelAppPdfGenerator;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A sample file to run misc tests.
 */
@Category(gov.nysenate.ess.core.annotation.SillyTest.class)
public class SillyTest extends BaseTest {
    private static final Logger logger = LoggerFactory.getLogger(SillyTest.class);
    // https://stackoverflow.com/questions/19635275/how-to-generate-multiple-lines-in-pdf-using-apache-pdfbox/19683618#19683618

    @Autowired TravelApplicationDao appDao;

    @Test
    public void realPdf() throws IOException {
        File file = new File("/home/kevin/travel-app.pdf");
        FileOutputStream fos = new FileOutputStream(file);
        if (!file.exists()) {
            file.createNewFile();
        }
//        TravelApplication app = appDao.selectTravelApplication(8);
        TravelApplication app = appDao.selectTravelApplication(9);
        TravelAppPdfGenerator pdfWriter = new TravelAppPdfGenerator(app);
        pdfWriter.write(fos);
        fos.flush();
        fos.close();
    }
}