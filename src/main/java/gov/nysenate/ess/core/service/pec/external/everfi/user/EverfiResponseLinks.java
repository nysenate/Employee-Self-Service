package gov.nysenate.ess.core.service.pec.external.everfi.user;

public class EverfiResponseLinks {
    private String self;
    private String first;
    private String prev;
    private String next;
    private String last;

    public EverfiResponseLinks() {
    }

    public String getSelf() {
        return self;
    }

    public String getFirst() {
        return first;
    }

    public String getPrev() {
        return prev;
    }

    public String getNext() {
        return next;
    }

    public String getLast() {
        return last;
    }

    @Override
    public String toString() {
        return "EverfiResponseLinks{" +
                "self='" + self + '\'' +
                ", first='" + first + '\'' +
                ", prev='" + prev + '\'' +
                ", next='" + next + '\'' +
                ", last='" + last + '\'' +
                '}';
    }
}
