package gov.nysenate.ess.travel.application;

import gov.nysenate.ess.core.util.DateUtils;
import gov.nysenate.ess.travel.application.allowances.PerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiem;
import gov.nysenate.ess.travel.application.allowances.lodging.LodgingPerDiems;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiem;
import gov.nysenate.ess.travel.application.allowances.meal.MealPerDiems;
import gov.nysenate.ess.travel.application.route.Route;
import gov.nysenate.ess.travel.application.route.destination.Destination;
import gov.nysenate.ess.travel.provider.senate.SenateMie;
import gov.nysenate.ess.travel.provider.senate.SqlSenateMieDao;
import gov.nysenate.ess.travel.utils.Dollars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class AmendmentService {

    private static final Logger logger = LoggerFactory.getLogger(AmendmentService.class);
    private SqlSenateMieDao senateMieDao;

    @Autowired
    public AmendmentService(SqlSenateMieDao senateMieDao) {
        this.senateMieDao = senateMieDao;
    }

    /**
     * Updates an Amendment's Route, MealPerDiems and LodgingPerDiems.
     * Meal and Lodging per diems are recalculated for the new route.
     * Use this method whenever setting a new route or changing a route on an amendment.
     * @param amd
     * @param route
     */
    public void setRoute(Amendment amd, Route route) {
        amd.setRoute(route);
        updateMealPerDiems(amd, route);
        updateLodgingPerDiems(amd, route);
    }

    /**
     * Create MealPerDiems for the given Route and set on given amendment.
     * @param amd
     * @param route
     */
    private void updateMealPerDiems(Amendment amd, Route route) {
        Set<MealPerDiem> mealPerDiemSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (PerDiem pd : d.mealPerDiems()) {
                SenateMie mie = null;
                try {
                    mie = senateMieDao.selectSenateMie(DateUtils.getFederalFiscalYear(pd.getDate()), new Dollars(pd.getRate()));
                } catch (IncorrectResultSizeDataAccessException ex) {
                    logger.warn("Unable to find Senate mie for date: " + pd.getDate().toString() + " and total: " + pd.getRate().toString());
                }
                mealPerDiemSet.add(new MealPerDiem(d.getAddress(), pd.getDate(), new Dollars(pd.getRate()), mie));
            }
        }
        amd.setMealPerDiems(new MealPerDiems(mealPerDiemSet));
    }

    /**
     * Create LodgingPerDiems for the given Route and set on the given Amendment.
     * @param amd
     * @param route
     */
    private void updateLodgingPerDiems(Amendment amd, Route route) {
        Set<LodgingPerDiem> lodgingPerDiemSet = new HashSet<>();
        for (Destination d : route.destinations()) {
            for (PerDiem pd : d.lodgingPerDiems()) {
                lodgingPerDiemSet.add(new LodgingPerDiem(d.getAddress(), pd));
            }
        }
        amd.setLodingPerDiems(new LodgingPerDiems(lodgingPerDiemSet));
    }
}
