/**
 * Service providing methods for handling REST api error responses.
 */
angular.module('ess').service('RestErrorService', [
    '$location', 'modals',
    function ($location, modals) {

        return {
            handleErrorResponse: handleErrorResponse
        };

        function handleErrorResponse(resp) {
            var errorCode = (resp.data.status || {}).code;
            if (errorCode === "UNAUTHENTICATED") {
                console.error('user unauthenticated, redirecting to login');
                $location.path(appProps.loginUrl);
            } else {
                console.error("Request error:", resp);
                modals.open('500', {details: resp});
            }
        }
    }]);