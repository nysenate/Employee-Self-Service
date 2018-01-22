package gov.nysenate.ess.travel.allowance.gsa.model;

import com.google.common.collect.ImmutableSet;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Contains a {@link LodgingNight} for all nights of a trip.
 */
public class LodgingAllowance {

    private final ImmutableSet<LodgingNight> nights;

    public LodgingAllowance() {
        this(ImmutableSet.of());
    }

    public LodgingAllowance(Set<LodgingNight> nights) {
        this(ImmutableSet.copyOf(nights));
    }

    public LodgingAllowance(ImmutableSet<LodgingNight> nights) {
        this.nights = nights;
    }

    public LodgingAllowance add(LodgingAllowance allowance) {
        return new LodgingAllowance(ImmutableSet.<LodgingNight>builder()
                .addAll(getNights())
                .addAll(allowance.getNights())
                .build());
    }

    public LodgingAllowance addNight(LodgingNight night) {
        return new LodgingAllowance(ImmutableSet.<LodgingNight>builder()
                .addAll(getNights())
                .add(night)
                .build());
    }

    public BigDecimal getTotal() {
        return getNights().stream()
                .map(LodgingNight::getRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public ImmutableSet<LodgingNight> getNights() {
        return nights;
    }

    @Override
    public String toString() {
        return "LodgingAllowance{" +
                "nights=" + nights +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LodgingAllowance that = (LodgingAllowance) o;
        return Objects.equals(nights, that.nights);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nights);
    }
}
