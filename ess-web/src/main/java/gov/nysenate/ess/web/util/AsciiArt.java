package gov.nysenate.ess.web.util;

public enum AsciiArt
{
    TS_LOGO("\n\n" +
        "███████╗███████╗███████╗ \n" +
        "██╔════╝██╔════╝██╔════╝ \n" +
        "█████╗  ███████╗███████╗ \n" +
        "██╔══╝  ╚════██║╚════██║ \n" +
        "███████╗███████║███████║ \n" +
        "╚══════╝╚══════╝╚══════╝ \n" +
        "Deployment on DATE\n");

    String text;

    AsciiArt(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
