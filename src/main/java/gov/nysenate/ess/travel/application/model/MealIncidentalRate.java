package gov.nysenate.ess.travel.application.model;

public class MealIncidentalRate {

    private int totalCost;
    private int breakfastCost;
    private int dinnerCost;
    private int incidentalCost;

    public MealIncidentalRate(int totalCost, int breakfastCost, int dinnerCost, int incidentalCost) {
        this.totalCost = totalCost;
        this.breakfastCost = breakfastCost;
        this.dinnerCost = dinnerCost;
        this.incidentalCost = incidentalCost;
    }

    public int getTotalCost() {
        return totalCost;
    }

    public int getBreakfastCost() {
        return breakfastCost;
    }

    public int getDinnerCost() {
        return dinnerCost;
    }

    public int getIncidentalCost() {
        return incidentalCost;
    }
}
