package gov.nysenate.ess.travel.fixtures;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Contains raw text json responses from the GSA Api to be used in tests.
 */
public class GsaApiResponseFixture {

    public static String fy2018_zip10008_response() throws IOException {
        String path = GsaApiResponseFixture.class.getClassLoader().getResource("travel/gsa_api_responses/gsa_response_for_fy2018_zip10008.txt").getFile();
        return FileUtils.readFileToString(new File(path), Charset.defaultCharset());
    }

    public static String fy2018_zip10940_response() throws IOException {
        String path = GsaApiResponseFixture.class.getClassLoader().getResource("travel/gsa_api_responses/gsa_response_for_fy2018_zip10940.txt").getFile();
        return FileUtils.readFileToString(new File(path), Charset.defaultCharset());
    }

    // Empty response for year with no data.
    public static String fy2999_zip11111_response() throws IOException {
        String path = GsaApiResponseFixture.class.getClassLoader().getResource("travel/gsa_api_responses/gsa_response_for_fy2999_zip11111.txt").getFile();
        return FileUtils.readFileToString(new File(path), Charset.defaultCharset());
    }
}
