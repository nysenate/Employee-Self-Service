package gov.nysenate.ess.travel.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * A wrapper around BigDecimal used to represent dollars.
 * Auto scales and rounds to two digits.
 */
public final class Dollars implements Comparable<Dollars> {
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    public static final Dollars ZERO = new Dollars(0);

    private final BigDecimal dollars;

    public Dollars(BigDecimal dollars) {
        this.dollars = dollars.setScale(SCALE, ROUNDING_MODE);
    }

    public Dollars(String dollars) {
        this(new BigDecimal(dollars == null ? "0" : dollars));
    }

    public Dollars(double dollars) {
        this(new BigDecimal(dollars));
    }

    public Dollars add(Dollars dollars) {
        return new Dollars(this.dollars.add(dollars.dollars));
    }

    public Dollars multiply(Dollars dollars) {
        return new Dollars(this.dollars.multiply(dollars.dollars));
    }

    public Dollars divide(int divisor) {
        return new Dollars(dollars.divide(new BigDecimal(divisor)));
    }

    public double toDouble() {
        return dollars.doubleValue();
    }

    @Override
    public String toString() {
        return dollars.toString();
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
