package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Allowances {

    protected Map<AllowanceType, Allowance> typeToAllowance;

    public Allowances() {
        // Initialize all allowances to $0
        typeToAllowance = EnumSet.allOf(AllowanceType.class).stream()
                .collect(Collectors.toMap(Function.identity(), type -> new Allowance(type, Dollars.ZERO)));
    }

    public Dollars total() {
        return typeToAllowance.values().stream()
                .map(a -> a.dollars)
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
        return typeToAllowance.get(AllowanceType.MILEAGE).dollars;
    }

    public void setMileage(Dollars dollars) {
        typeToAllowance.get(AllowanceType.MILEAGE).dollars = dollars;
    }

    public Dollars meals() {
        return typeToAllowance.get(AllowanceType.MEALS).dollars;
    }

    public void setMeals(Dollars dollars) {
        typeToAllowance.get(AllowanceType.MEALS).dollars = dollars;
    }

    public Dollars lodging() {
        return typeToAllowance.get(AllowanceType.LODGING).dollars;
    }

    public void setLodging(Dollars dollars) {
        typeToAllowance.get(AllowanceType.LODGING).dollars = dollars;
    }

    public Dollars tolls() {
        return typeToAllowance.get(AllowanceType.TOLLS).dollars;
    }

    public void setTolls(Dollars dollars) {
        typeToAllowance.get(AllowanceType.TOLLS).dollars = dollars;
    }

    public Dollars parking() {
        return typeToAllowance.get(AllowanceType.PARKING).dollars;
    }

    public void setParking(Dollars dollars) {
        typeToAllowance.get(AllowanceType.PARKING).dollars = dollars;
    }

    public Dollars trainAndPlane() {
        return typeToAllowance.get(AllowanceType.TRAIN_AND_PLANE).dollars;
    }

    public void setTrainAndPlane(Dollars dollars) {
        typeToAllowance.get(AllowanceType.TRAIN_AND_PLANE).dollars = dollars;
    }

    public Dollars alternateTransportation() {
        return typeToAllowance.get(AllowanceType.ALTERNATE_TRANSPORTATION).dollars;
    }

    public void setAlternateTransportation(Dollars dollars) {
        typeToAllowance.get(AllowanceType.ALTERNATE_TRANSPORTATION).dollars = dollars;
    }

    public Dollars registration() {
        return typeToAllowance.get(AllowanceType.REGISTRATION).dollars;
    }

    public void setRegistration(Dollars dollars) {
        typeToAllowance.get(AllowanceType.REGISTRATION).dollars = dollars;
    }

    @Override
    public String toString() {
        return "Allowances{" +
                "typeToAllowance=" + typeToAllowance +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Allowances that = (Allowances) o;
        return Objects.equals(typeToAllowance, that.typeToAllowance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeToAllowance);
    }
}
