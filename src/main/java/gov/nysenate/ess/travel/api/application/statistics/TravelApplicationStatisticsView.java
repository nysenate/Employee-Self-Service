package gov.nysenate.ess.travel.api.application.statistics;

import gov.nysenate.ess.core.client.view.base.ViewObject;

public class TravelApplicationStatisticsView implements ViewObject {
    TravelApplicationStatisticsByStatusView travelStatusStatisticsView;
    TravelApplicationStatisticsByEmployeeView travelEmployeeStatisticsView;

    public TravelApplicationStatisticsView(TravelApplicationStatisticsByStatusView travelStatusStatisticsView, TravelApplicationStatisticsByEmployeeView travelEmployeeStatisticsView) {
        this.travelStatusStatisticsView = travelStatusStatisticsView;
        this.travelEmployeeStatisticsView = travelEmployeeStatisticsView;
    }

    public TravelApplicationStatisticsByStatusView getTravelStatusStatisticsView() {
        return travelStatusStatisticsView;
    }

    public void setTravelStatusStatisticsView(TravelApplicationStatisticsByStatusView travelStatusStatisticsView) {
        this.travelStatusStatisticsView = travelStatusStatisticsView;
    }

    public TravelApplicationStatisticsByEmployeeView getTravelEmployeeStatisticsView() {
        return travelEmployeeStatisticsView;
    }

    public void setTravelEmployeeStatisticsView(TravelApplicationStatisticsByEmployeeView travelEmployeeStatisticsView) {
        this.travelEmployeeStatisticsView = travelEmployeeStatisticsView;
    }

    @Override
    public String getViewType() {
        return "travel_application_statistics";
    }
}
