package gov.nysenate.ess.time.controller.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

/**
 * A simple class for handling requests for time record PDFs.
 */
@RestController
@RequestMapping("/time/record/history/pdf")
public class AttendanceRecordPdfCtrl {
    @Value("${sfms.report.base.url}")
    private String baseUrl;

    /**
     * Gets the data from SFMS, and sends it as bytes.
     * The endDate parameter just exists to set a useful filename.
     * @param timeRecordId to obtain the PDF of.
     * @return the bytes of the PDF.
     */
    @RequestMapping("/{endDate}")
    public ResponseEntity<byte[]> getPdf(@RequestParam String timeRecordId) throws IOException {
        URL url = new URL(baseUrl + "?report=PRTIMESHEET23&cmdkey=tsuser" +
                "&p_stamp=N&p_nuxrtimesheet=" + timeRecordId);
        return ResponseEntity.ok().contentType(MediaType.parseMediaType(MediaType.APPLICATION_PDF_VALUE))
                .body(url.openStream().readAllBytes());
    }
}
