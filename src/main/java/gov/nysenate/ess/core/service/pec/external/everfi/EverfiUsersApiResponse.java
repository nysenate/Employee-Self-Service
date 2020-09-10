package gov.nysenate.ess.core.service.pec.external.everfi;

import java.util.List;

public class EverfiUsersApiResponse {

    // TODO WIP!

    public List<EverfiUser> data;
    public EverfiResponseLinks links;
    public EverfiResponseMeta meta;

    public List<EverfiUser> getData() {
        return data;
    }

    public EverfiResponseLinks getLinks() {
        return links;
    }

    public EverfiResponseMeta getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "EverfiUsersApiResponse{" +
                "data=" + data +
                ", links=" + links +
                ", meta=" + meta +
                '}';
    }

    private class EverfiResponseLinks {
        public String self;
        public String first;
        public String prev;
        public String next;
        public String last;

        public EverfiResponseLinks() {
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

    private class EverfiResponseMeta {
        public int total_count;
        public int cursor_id;

        public EverfiResponseMeta() {
        }

        @Override
        public String toString() {
            return "EverfiResponseMeta{" +
                    "total_count=" + total_count +
                    ", cursor_id=" + cursor_id +
                    '}';
        }
    }
}
