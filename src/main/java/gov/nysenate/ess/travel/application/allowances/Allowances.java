package gov.nysenate.ess.travel.application.allowances;

import gov.nysenate.ess.travel.utils.Dollars;

import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Contains allowances which are entered by the traveler.
 */
public class Allowances {

    Map<AllowanceType, Allowance> typeToAllowance;

    public Allowances() {
        // Initialize all allowances to $0
        typeToAllowance = EnumSet.allOf(AllowanceType.class).stream()
                .collect(Collectors.toMap(Function.identity(), type -> new Allowance(type, Dollars.ZERO)));
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
