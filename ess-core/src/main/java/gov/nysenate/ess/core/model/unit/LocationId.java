package gov.nysenate.ess.core.model.unit;

/**
 * The location code together with the location type uniquely identify a location.
 */
public final class LocationId {

    private final String code;
    private final LocationType type;

    public LocationId(String code, LocationType type) {
        this.code = code;
        this.type = type;
    }

    public LocationId(String locCode, char locType) {
        this.code = locCode;
        this.type = LocationType.valueOfCode(locType);
    }

    /** Creates a location Id from its toString() output. */
    public static LocationId ofString(String locString) {
        String[] parts = locString.split("\\-");
        return new LocationId(parts[0].trim(), parts[1].trim().charAt(0));
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

    @Override
    public String toString() {
        return code + '-' + type.getCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationId that = (LocationId) o;
        if (code != null ? !code.equals(that.code) : that.code != null) return false;
        return type == that.type;
    }

    @Override
    public int hashCode() {
        int result = code != null ? code.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
