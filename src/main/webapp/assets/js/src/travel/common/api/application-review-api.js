angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var appReviewApi = $resource(appProps.apiPath + '/travel/review.json');
        var approveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/approve.json', {approvalId: '@appReviewId'});
        var disapproveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/disapprove.json', {approvalId: '@appReviewId'});

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         */
        function pendingReviews () {
            return appReviewApi.get({}).$promise
                .then(getResult)
                .catch(restErrorService.handleErrorResponse);
        }

        function approve (appReviewId) {
            return approveApi.save({appReviewId: appReviewId}).$promise
                .catch(restErrorService.handleErrorResponse);
        }

        function disapprove (appReviewId) {
            return disapproveApi.save({appReviewId: appReviewId}).$promise
                .catch(restErrorService.handleErrorResponse);
        }

        function getResult(response) {
            return response.result;
        }

        return {
            pendingReviews: pendingReviews,
            approve: approve,
            disapprove: disapprove
        }
    }
]);