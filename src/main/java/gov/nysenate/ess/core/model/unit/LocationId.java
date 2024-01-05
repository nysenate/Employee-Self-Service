package gov.nysenate.ess.core.model.unit;

/**
 * The location code together with the location type uniquely identify a location.
 */
public record LocationId(String code, LocationType type) {
    public LocationId(String locCode, char locType) {
        this(locCode, LocationType.valueOfCode(locType));
        // TODO error if locType is invalid. isSyntacticallyValid kinda handles this but is never called?
    }

    /** Creates a location Id from its toString() output. */
    public static LocationId ofString(String locString) {
        // Don't throw IllegalArgumentException if missing '-'
        if (locString == null || !locString.contains("-")) {
            return new LocationId(null, null);
        }
        String[] parts = locString.split("-");
        return new LocationId(parts[0], LocationType.valueOfCode(parts[1].charAt(0)));
    }

    @Override
    // API calls rely on this.
    public String toString() {
        return code + '-' + type.getCode();
    }

    public String getCode() {
        return code;
    }

    public LocationType getType() {
        return type;
    }

    public String getTypeAsString() {
        return String.valueOf(type.getCode());
    }

    /**
     * Was this locationId constructed with valid syntax.
     * May want to replace with NullObjectPattern.
     *
     * @return <code>false</code> if its impossible for this to represent a {@link Location}.
     * <code>true</code> otherwise.
     */
    public boolean isSyntacticallyValid() {
        return code != null && type != null && !code.isEmpty();
    }
}
