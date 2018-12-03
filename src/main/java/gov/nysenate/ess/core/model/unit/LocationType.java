package gov.nysenate.ess.core.model.unit;

/**
 * Enumeration of all possible location codes
 */
public enum LocationType
{
    WAREHOUSE('H', "Warehouse Location"),
    STORAGE('S', "Storage Location"),
    SUPPLY('P', "Supply Location"),
    WORK('W', "Work Location");

    char code;
    String name;

    LocationType(char code, String name) {
        this.code = code;
        this.name = name;
    }

    public static LocationType valueOfCode(char code) {
        for (LocationType type : LocationType.values()) {
            code = Character.toUpperCase(code);
            if (type.code == code) {
                return type;
            }
        }
        return null;
    }

    public char getCode() {
        return code;
    }

    public String fullName() {
        return name;
    }
}