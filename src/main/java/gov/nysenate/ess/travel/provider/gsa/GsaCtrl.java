package gov.nysenate.ess.travel.provider.gsa;

import com.fasterxml.jackson.core.JsonProcessingException;
import gov.nysenate.ess.core.client.view.base.StringView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.travel.provider.gsa.model.GsaInfo;
import gov.nysenate.ess.travel.utils.Dollars;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/gsa")
public class GsaCtrl extends BaseRestApiCtrl {

    private GsaBatchResponseService gsaBatchResponseService;
    private GsaApi gsaApi;
    private GsaAllowanceService gsaAllowanceService;
    private GsaLocalAllowanceService gsaLocalAllowanceService;

    @Autowired
    public GsaCtrl(GsaBatchResponseService gsaBatchResponseService, GsaApi gsaApi, GsaAllowanceService gsaAllowanceService, GsaLocalAllowanceService gsaLocalAllowanceService) {
        this.gsaBatchResponseService = gsaBatchResponseService;
        this.gsaApi = gsaApi;
        this.gsaAllowanceService = gsaAllowanceService;
        this.gsaLocalAllowanceService = gsaLocalAllowanceService;
    }

    @RequestMapping(value = "/backup")
    public void backup() throws IOException {
        List<GsaInfo> allZipCodes = new ArrayList<>();
        String backup = "/home/nystech/Senate_Code/Employee-Self-Service/src/main/java/gov/nysenate/ess/travel/provider/gsa/backup/";
        String archive = "/home/nystech/Senate_Code/Employee-Self-Service/src/main/java/gov/nysenate/ess/travel/provider/gsa/archive/";

        Path dir = Paths.get(backup);
        for (File file : dir.toFile().listFiles()) {
            try {
                Path backupDirectory = Paths.get(
                        backup + file.getName());
                Path archiveirectory = Paths.get(
                        archive + file.getName());
                BufferedReader br = new BufferedReader(new FileReader("/home/nystech/Senate_Code/Employee-Self-Service/src/main/java/gov/nysenate/ess/travel/provider/gsa/backup/" + file.getName()));
                br.readLine();
                String line;
                while ((line = br.readLine()) != null) {
                    GsaInfo gsaZipCode = new GsaInfo();
                    String[] parts = line.split(",");
                    if (parts[1].equals("District of Columbia")) {
                        gsaZipCode.setCity(parts[1]);
                        gsaZipCode.setCounty(parts[2]);
                        gsaZipCode.setMeals(Integer.parseInt(parts[parts.length - 1]));
                    } else if (parts[2].isEmpty() && !parts[1].contains("Standard Rate") && parts[4].isEmpty()) {
                        gsaZipCode.setCity(parts[1]);
                        gsaZipCode.setCounty(null);
                        gsaZipCode.setMeals(Integer.parseInt(parts[parts.length - 1]));
                    } else if (Integer.parseInt(parts[0]) == 0) {
                        gsaZipCode.setMeals(Integer.parseInt(parts[parts.length - 1]));
                        gsaZipCode.setCounty(null);
                        gsaZipCode.setCity(null);
                    } else {
                        gsaZipCode.setCity(parts[1]);
                        gsaZipCode.setCounty(parts[2] + "," + parts[3]);
                        gsaZipCode.setMeals(Integer.parseInt(parts[parts.length - 1]));
                    }
                    for (int i = parts.length - 1; i >= 0; i--) {
                        if (parts[i].length() == 4) {
                            gsaZipCode.setFiscalYear(Integer.parseInt(parts[i]));
                            gsaZipCode.setZipCode(parts[i - 1]);
                            break;
                        }
                    }
                    HashMap<Month, BigDecimal> lodgingRates = new HashMap<>();
                    lodgingRates.put(Month.SEPTEMBER, new BigDecimal(parts[parts.length - 2]));
                    lodgingRates.put(Month.AUGUST, new BigDecimal(parts[parts.length - 3]));
                    lodgingRates.put(Month.JULY, new BigDecimal(parts[parts.length - 4]));
                    lodgingRates.put(Month.JUNE, new BigDecimal(parts[parts.length - 5]));
                    lodgingRates.put(Month.MAY, new BigDecimal(parts[parts.length - 6]));
                    lodgingRates.put(Month.APRIL, new BigDecimal(parts[parts.length - 7]));
                    lodgingRates.put(Month.MARCH, new BigDecimal(parts[parts.length - 8]));
                    lodgingRates.put(Month.FEBRUARY, new BigDecimal(parts[parts.length - 9]));
                    lodgingRates.put(Month.JANUARY, new BigDecimal(parts[parts.length - 10]));
                    lodgingRates.put(Month.DECEMBER, new BigDecimal(parts[parts.length - 11]));
                    lodgingRates.put(Month.NOVEMBER, new BigDecimal(parts[parts.length - 12]));
                    lodgingRates.put(Month.OCTOBER, new BigDecimal(parts[parts.length - 13]));

                    gsaZipCode.setLodgingRates(lodgingRates);
                    allZipCodes.add(gsaZipCode);
                }
                gsaBatchResponseService.saveGsaData(allZipCodes);
                Files.move(backupDirectory, archiveirectory);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

    }

    @GetMapping(value = "/lodgingRates")
    public Dollars getLodgingRates(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date, String zip5) throws JsonProcessingException {
        return gsaLocalAllowanceService.fetchLodgingRate(date, zip5);
    }

    @GetMapping(value = "/meals")
    public String getMeals(@RequestParam String zip5) throws JsonProcessingException {
        return gsaLocalAllowanceService.fetchMealsRate(zip5);
    }

    @RequestMapping(value = "/batch")
    public StringView updateGsaInformation() throws IOException {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());
        boolean success = gsaBatchResponseService.cycleThroughGsaInfo();
        String responseText = "";
        if (success) {
            responseText = "Success: The GSA data was parsed and stored successfully";
        } else {
            responseText = "Failure: The GSA data was not updated";
        }
        return new StringView(responseText);
    }

    @RequestMapping(value = "/{zip}")
    public StringView updateGsaInformation(@PathVariable String zip) {
        checkPermission(SimpleEssPermission.ADMIN.getPermission());

        GsaResponse gsaResponse = gsaApi.queryGsaApi(LocalDate.now(), zip);
        String responseText = "";
        if (gsaResponse != null) {
            responseText = "Success: " + gsaResponse;
        } else {
            responseText = "Failure: The GSA data was not updated";
        }
        return new StringView(responseText);
    }
}
