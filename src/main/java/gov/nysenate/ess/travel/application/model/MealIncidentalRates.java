package gov.nysenate.ess.travel.application.model;

public enum MealIncidentalRates {
    $51("11", "23", "5"),
    $54("12", "25", "5"),
    $59("13", "26", "5"),
    $64("15", "28", "5"),
    $69("16", "31", "5"),
    $74("17", "34", "5");

    private String breakfastCost;
    private String dinnerCost;
    private String incidentalCost;

    MealIncidentalRates(String breakfastCost, String dinnerCost, String incidentalCost) {
        this.breakfastCost = breakfastCost;
        this.dinnerCost = dinnerCost;
        this.incidentalCost = incidentalCost;
    }

    public String getBreakfastCost() {
        return breakfastCost;
    }

    public String getDinnerCost() {
        return dinnerCost;
    }

    public String getIncidentalCost() {
        return incidentalCost;
    }
}
