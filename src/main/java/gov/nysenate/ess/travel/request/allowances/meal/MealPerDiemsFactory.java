package gov.nysenate.ess.travel.request.allowances.meal;

import gov.nysenate.ess.core.service.notification.slack.service.SlackChatService;
import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.employee.TravelEmployee;
import gov.nysenate.ess.travel.provider.gsa.GsaAllowanceService;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SqlSenateMieDao;
import gov.nysenate.ess.travel.request.allowances.PerDiem;
import gov.nysenate.ess.travel.request.route.Route;
import gov.nysenate.ess.travel.request.route.destination.Destination;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class MealPerDiemsFactory {

    private static final Logger logger = LoggerFactory.getLogger(MealPerDiemsFactory.class);
    private final static Comparator<MealPerDiem> dateComparator = Comparator.comparing(MealPerDiem::date);
    private final SqlSenateMieDao senateMieDao;
    private final SlackChatService slackChatService;
    private final GsaAllowanceService gsaAllowanceService;

    @Autowired
    public MealPerDiemsFactory(SqlSenateMieDao senateMieDao, SlackChatService slackChatService, GsaAllowanceService gsaAllowanceService) {
        this.senateMieDao = senateMieDao;
        this.slackChatService = slackChatService;
        this.gsaAllowanceService = gsaAllowanceService;
    }

    public MealPerDiems create(Route route, TravelEmployee traveler) {
        boolean isAllowedMeals = traveler.getRespCenter().isAdministrativeOffice();
        MealPerDiemAdjustments adjustments = new MealPerDiemAdjustments.Builder()
                .withIsAllowedMeals(isAllowedMeals)
                .build();
        Set<MealPerDiem> mealPerDiemSet = init(route);
        List<MealPerDiem> mpds = dedupe(mealPerDiemSet);
        mpds.sort(dateComparator);
        if (mpds.size() > 0) {
            mpds.get(0).setQualifiesForBreakfast(route.firstLegQualifiesForBreakfast());
            mpds.get(mpds.size() - 1).setQualifiesForDinner(route.lastLegQualifiesForDinner());
        }
        return new MealPerDiems(mpds, adjustments);
    }

    private Set<MealPerDiem> init(Route route) {
        Set<MealPerDiem> mealPerDiemSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (LocalDate date : d.days()) {
                Dollars mieTotal = gsaAllowanceService.fetchMealRate(date, d.getAddress().getZip5());
                // Ignore zero rates... TODO is this correct?
                if (!mieTotal.equals(Dollars.ZERO)) {
                    SenateMie mie = null;
                    try {
                        mie = senateMieDao.selectSenateMie(DateUtils.getFederalFiscalYear(date), mieTotal);
                    } catch (IncorrectResultSizeDataAccessException ex) {
                        sendSenateMieMissingErrorMessages(date, mieTotal);
                    }
                    mealPerDiemSet.add(new MealPerDiem(d.getAddress(), date, mieTotal, mie));
                }
            }
        }
        return mealPerDiemSet;
    }

    private void sendSenateMieMissingErrorMessages(LocalDate date, Dollars rate) {
        String msg = """
                     Unable to find Senate MIE rates for date %s, and total %s.
                     If the Senate has not published their rates yet, you may estimate them from the GSA rates available at
                     https://api.gsa.gov/travel/perdiem/v2/rates/conus/mie/{year}?api_key=
                     Using the typical rules: senate.breakfast = gsa.breakfast, senate.dinner = gsa.dinner + gsa.lunch + gsa.incidental
                     """.formatted(date, rate.toString());
        slackChatService.sendMessage(msg);
        logger.warn(msg);
    }

    // There can only be one meal per diem per day. This keeps the highest rate meal per diem for each day.
    private List<MealPerDiem> dedupe(Set<MealPerDiem> mealPerDiemSet) {
        Map<LocalDate, MealPerDiem> dateToPerDiems = new HashMap<>();
        for (MealPerDiem mpd : mealPerDiemSet) {
            if (dateToPerDiems.containsKey(mpd.date())) {
                // Replace if this rate is higher.
                if (mpd.rate().compareTo(dateToPerDiems.get(mpd.date()).rate()) > 0) {
                    dateToPerDiems.put(mpd.date(), mpd);
                }
            } else {
                dateToPerDiems.put(mpd.date(), mpd);
            }
        }
        return new ArrayList<>(dateToPerDiems.values());
    }
}
