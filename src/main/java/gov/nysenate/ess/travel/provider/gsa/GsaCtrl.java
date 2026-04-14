package gov.nysenate.ess.travel.provider.gsa;

import gov.nysenate.ess.core.client.view.base.StringView;
import gov.nysenate.ess.core.controller.api.BaseRestApiCtrl;
import gov.nysenate.ess.core.model.auth.SimpleEssPermission;
import gov.nysenate.ess.travel.provider.gsa.model.GsaInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/gsa")
public class GsaCtrl extends BaseRestApiCtrl {

    private GsaBatchResponseService gsaBatchResponseService;
    private GsaApi gsaApi;
    private GsaAllowanceService gsaAllowanceService;

    @Autowired
    public GsaCtrl(GsaBatchResponseService gsaBatchResponseService, GsaApi gsaApi, GsaAllowanceService gsaAllowanceService) {
        this.gsaBatchResponseService = gsaBatchResponseService;
        this.gsaApi = gsaApi;
        this.gsaAllowanceService = gsaAllowanceService;
    }

    @RequestMapping(value = "/backup")
    public void backup() throws IOException {
        List<GsaInfo> allZipCodes = new ArrayList<>();
        int c = 0;
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
                            gsaZipCode.setZipCode(Integer.parseInt(parts[i - 1]));
                            break;
                        }
                    }
                    gsaZipCode.setLodgingRates(new HashMap<>());
                    allZipCodes.add(gsaZipCode);
                }
                gsaBatchResponseService.saveGsaData(allZipCodes);
                Files.move(backupDirectory, archiveirectory);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

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
