package gov.nysenate.ess.core.service.pec.external.everfi;

import com.fasterxml.jackson.annotation.JsonProperty;

public final class OAuthTokenResponse {
    @JsonProperty("access_token")
    public String accessToken;

    @JsonProperty("token_type")
    public String tokenType;

    @JsonProperty("expires_in")
    public long expiresIn;

    @JsonProperty("created_at")
    public long createdAt;

    @JsonProperty("expires_at")
    public long expiresAt;
}
