package gov.nysenate.ess.web.util;

public enum AsciiArt
{
    TS_LOGO("""



            8888888888        .d8888b.         .d8888b. \s
            888              d88P  Y88b       d88P  Y88b\s
            888              Y88b.            Y88b.     \s
            8888888           "Y888b.          "Y888b.  \s
            888                  "Y88b.           "Y88b.\s
            888                    "888             "888\s
            888              Y88b  d88P       Y88b  d88P\s
            8888888888        "Y8888P"         "Y8888P"


            Deployment on DATE
            """);

    private final String text;

    AsciiArt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
