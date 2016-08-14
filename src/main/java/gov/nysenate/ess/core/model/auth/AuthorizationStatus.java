package gov.nysenate.ess.core.model.auth;

public enum AuthorizationStatus
{
   AUTHORIZED(true, "Authorized"),
   UNAUTHORIZED(false, "Unauthorized access. The user does not have the necessary permissions."),
   UNAUTHENTICATED(false, "Authentication is required before proceeding.");

   private boolean authorized;
   private String message;

   AuthorizationStatus(boolean authorized, String message) {
       this.authorized = authorized;
       this.message = message;
   }

    public boolean isAuthorized() {
        return authorized;
    }

    public String getMessage() {
        return message;
    }
}
