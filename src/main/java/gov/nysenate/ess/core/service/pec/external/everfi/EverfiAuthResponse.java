package gov.nysenate.ess.core.service.pec.external.everfi;

public class EverfiAuthResponse {

    public String access_token;
    public String token_type;
    public String expires_in;
    public String created_at;

    @Override
    public String toString() {
        return "EverfiAuthResponse{" +
                "access_token='" + access_token + '\'' +
                ", token_type='" + token_type + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", created_at='" + created_at + '\'' +
                '}';
    }
}
