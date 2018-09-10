package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * A wrapper around BigDecimal used to represent dollars.
 * Auto scales and rounds to two digits.
 */
public final class Dollars implements Comparable<Dollars> {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    public static final Dollars ZERO = new Dollars("0");

    private final BigDecimal dollars;

    public Dollars(BigDecimal dollars) {
        dollars = dollars.setScale(SCALE, ROUNDING_MODE);
        this.dollars = dollars;
    }

    public Dollars(String dollars) {
        this(new BigDecimal(dollars));
    }

    public Dollars(double dollars) {
        this(new BigDecimal(dollars));
    }

    public Dollars add(Dollars dollars) {
        return new Dollars(this.getDollars().add(dollars.getDollars()));
    }

    public Dollars multiply(Dollars dollars) {
        return new Dollars(this.getDollars().multiply(dollars.getDollars()));
    }

    private BigDecimal getDollars() {
        return dollars;
    }

    @Override
    public String toString() {
        return this.dollars.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dollars dollars1 = (Dollars) o;
        return Objects.equals(dollars, dollars1.dollars);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dollars);
    }

    @Override
    public int compareTo(Dollars o) {
        return dollars.compareTo(o.dollars);
    }
}
