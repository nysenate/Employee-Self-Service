package gov.nysenate.ess.web.controller.page;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/login")
public class LoginPageCtrl
{
    private static final Logger logger = LoggerFactory.getLogger(LoginPageCtrl.class);

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.HEAD})
    public String loginPage(HttpServletRequest request, HttpServletResponse response) {
        return "login";
    }
}
