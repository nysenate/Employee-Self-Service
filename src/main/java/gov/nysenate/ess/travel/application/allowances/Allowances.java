package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.EnumSet;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Allowances {

    /**
     * Map of {@link AllowanceType} to the value of that allowance.
     */
    protected Map<AllowanceType, Dollars> typeToAllowance;

    public Allowances() {
        // Initialize all allowances to $0
        typeToAllowance = EnumSet.allOf(AllowanceType.class).stream()
                .collect(Collectors.toMap(Function.identity(), t -> Dollars.ZERO));
    }

    public Allowances(Map<AllowanceType, Dollars> typeToAllowance) {
        this.typeToAllowance = typeToAllowance;
    }

    public Dollars total() {
        return typeToAllowance.values().stream()
                .reduce(Dollars.ZERO, Dollars::add);
    }

    /**
     * Total transportation allowance.
     * Used as a field on the print form.
     */
    public Dollars transportation() {
        return mileage().add(trainAndPlane());
    }

    /**
     * Sum of tolls and parking allowances.
     * Used as a field on the print form.
     * @return
     */
    public Dollars tollsAndParking() {
        return tolls().add(parking());
    }

    public Dollars mileage() {
        return typeToAllowance.get(AllowanceType.MILEAGE);
    }

    public void setMileage(Dollars dollars) {
        typeToAllowance.put(AllowanceType.MILEAGE, dollars);
    }

    public Dollars meals() {
        return typeToAllowance.get(AllowanceType.MEALS);
    }

    public void setMeals(Dollars dollars) {
        typeToAllowance.put(AllowanceType.MEALS, dollars);
    }

    public Dollars lodging() {
        return typeToAllowance.get(AllowanceType.LODGING);
    }

    public void setLodging(Dollars dollars) {
        typeToAllowance.put(AllowanceType.LODGING, dollars);
    }

    public Dollars tolls() {
        return typeToAllowance.get(AllowanceType.TOLLS);
    }

    public void setTolls(Dollars dollars) {
        typeToAllowance.put(AllowanceType.TOLLS, dollars);
    }

    public Dollars parking() {
        return typeToAllowance.get(AllowanceType.PARKING);
    }

    public void setParking(Dollars dollars) {
        typeToAllowance.put(AllowanceType.PARKING, dollars);
    }

    public Dollars trainAndPlane() {
        return typeToAllowance.get(AllowanceType.TRAIN_AND_PLANE);
    }

    public void setTrainAndPlane(Dollars dollars) {
        typeToAllowance.put(AllowanceType.TRAIN_AND_PLANE, dollars);
    }

    public Dollars alternateTransportation() {
        return typeToAllowance.get(AllowanceType.ALTERNATE_TRANSPORTATION);
    }

    public void setAlternateTransportation(Dollars dollars) {
        typeToAllowance.put(AllowanceType.ALTERNATE_TRANSPORTATION, dollars);
    }

    public Dollars registration() {
        return typeToAllowance.get(AllowanceType.REGISTRATION);
    }

    public void setRegistration(Dollars dollars) {
        typeToAllowance.put(AllowanceType.REGISTRATION, dollars);
    }
}
