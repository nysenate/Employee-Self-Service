package gov.nysenate.ess.travel.allowance.gsa.model;

public enum MealIncidentalRates {
    $51(11, 23, 0),
    $54(12, 25, 0),
    $59(13, 26, 0),
    $64(15, 28, 0),
    $69(16, 31, 0),
    $74(17, 34, 0);

    private int breakfastCost;
    private int dinnerCost;
    private int incidentalCost;

    MealIncidentalRates(int breakfastCost, int dinnerCost, int incidentalCost) {
        this.breakfastCost = breakfastCost;
        this.dinnerCost = dinnerCost;
        this.incidentalCost = incidentalCost;
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
