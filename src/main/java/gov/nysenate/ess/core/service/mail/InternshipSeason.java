package gov.nysenate.ess.core.service.mail;

public enum InternshipSeason {

    SPRING("Spring"),
    SUMMER("Summer"),
    FALL("Fall");

    private final String season;

    InternshipSeason(String season) {
        this.season = season;
    }

    public String getSeason() {
        return season;
    }

}
