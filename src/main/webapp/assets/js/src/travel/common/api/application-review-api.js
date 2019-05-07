angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var pendingReviewsApi = $resource(appProps.apiPath + '/travel/review/pending.json');
        var reviewHistoryApi = $resource(appProps.apiPath + '/travel/review/history.json');
        var approveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/approve.json', {appReviewId: '@appReviewId'});
        var disapproveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/disapprove.json', {appReviewId: '@appReviewId'});

        var appReviewApi = {};

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         */
        appReviewApi.pendingReviews = function() {
            return pendingReviewsApi.get({}).$promise
                .then(getResult)
                .catch(restErrorService.handleErrorResponse);
        };

        appReviewApi.reviewHistory = function () {
            return reviewHistoryApi.get({}).$promise
                .then(getResult)
                .catch(restErrorService.handleErrorResponse);
        };

        appReviewApi.approve = function (appReviewId) {
            return approveApi.save({appReviewId: appReviewId}).$promise
                .catch(restErrorService.handleErrorResponse);
        };

        appReviewApi.disapprove = function (appReviewId) {
            return disapproveApi.save({appReviewId: appReviewId}).$promise
                .catch(restErrorService.handleErrorResponse);
        };

        function getResult(response) {
            return response.result;
        }

        return appReviewApi;
    }
]);