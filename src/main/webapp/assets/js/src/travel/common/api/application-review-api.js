angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var pendingReviewsApi = $resource(appProps.apiPath + '/travel/review/pending.json');
        var reviewHistoryApi = $resource(appProps.apiPath + '/travel/review/history.json');
        var approveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/approve.json', {appReviewId: '@appReviewId'});
        var disapproveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/disapprove.json', {appReviewId: '@appReviewId'});

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         */
        function pendingReviews () {
            return pendingReviewsApi.get({}).$promise
                .then(getResult)
                .catch(restErrorService.handleErrorResponse);
        }

        function reviewHistory () {
            return reviewHistoryApi.get({}).$promise
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
            reviewHistory: reviewHistory,
            approve: approve,
            disapprove: disapprove
        }
    }
]);