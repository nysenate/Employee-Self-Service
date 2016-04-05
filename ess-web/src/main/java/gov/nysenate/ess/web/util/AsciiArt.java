package gov.nysenate.ess.web.util;

public enum AsciiArt
{
    TS_LOGO("\n\n\n" +
            "8888888888        .d8888b.         .d8888b.  \n" +
            "888              d88P  Y88b       d88P  Y88b \n" +
            "888              Y88b.            Y88b.      \n" +
            "8888888           \"Y888b.          \"Y888b.   \n" +
            "888                  \"Y88b.           \"Y88b. \n" +
            "888                    \"888             \"888 \n" +
            "888              Y88b  d88P       Y88b  d88P \n" +
            "8888888888        \"Y8888P\"         \"Y8888P\"\n" +
            "\n\n" +
            "Deployment on DATE\n");

    String text;

    AsciiArt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
