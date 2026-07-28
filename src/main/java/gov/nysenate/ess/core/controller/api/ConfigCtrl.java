package gov.nysenate.ess.core.controller.api;

import gov.nysenate.ess.core.client.response.base.BaseResponse;
import gov.nysenate.ess.core.client.response.base.ViewObjectResponse;
import gov.nysenate.ess.core.config.RuntimeLevel;
import gov.nysenate.ess.core.view.ConfigView;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(BaseRestApiCtrl.REST_PATH + "/config")
public class ConfigCtrl extends BaseRestApiCtrl {

    private final RuntimeLevel runtimeLevel;
    private final String loginUrl;
    private final String googleApiKey;
    private final String helplinePhoneNumber;
    private final String personnelPhoneNumber;

    public ConfigCtrl(
            @Value("${runtime.level}") String runtimeLevel,
            @Value("${login.url}") String loginUrl,
            @Value("${google.maps.api.key}") String googleApiKey,
            @Value("${helpline.phone.number}") String helplinePhoneNumber,
            @Value("${personnel.phone.number}") String personnelPhoneNumber) {

        this.runtimeLevel = RuntimeLevel.of(runtimeLevel);
        this.loginUrl = loginUrl;
        this.googleApiKey = googleApiKey;
        this.helplinePhoneNumber = helplinePhoneNumber;
        this.personnelPhoneNumber = personnelPhoneNumber;
    }

    @RequestMapping("")
    public BaseResponse config() {
        ConfigView cv = new ConfigView();
        cv.put("runtimeLevel", runtimeLevel.name().toLowerCase());
        cv.put("loginUrl", loginUrl);
        cv.put("googleApiKey", googleApiKey);
        cv.put("helplinePhoneNumber", helplinePhoneNumber);
        cv.put("personnelPhoneNumber", personnelPhoneNumber);
        return new ViewObjectResponse<>(cv);
    }
}
