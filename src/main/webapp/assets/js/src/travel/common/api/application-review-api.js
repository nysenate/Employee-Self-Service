angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var pendingReviewsApi = $resource(appProps.apiPath + '/travel/review/pending.json',
                                          {},
                                          {get: {method: 'GET', cancellable: true}});
        var reviewHistoryApi = $resource(appProps.apiPath + '/travel/review/history.json',
                                         {},
                                         {get: {method: 'GET', cancellable: true}});
        var approveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/approve.json',
                                   {appReviewId: '@appReviewId'},
                                   {save: {method: 'POST', cancellable: true}});
        var disapproveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/disapprove.json',
                                      {appReviewId: '@appReviewId'},
                                      {save: {method: 'POST', cancellable: true}});

        var appReviewApi = {};

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         */
        appReviewApi.pendingReviews = function () {
            return pendingReviewsApi.get({});
        };

        appReviewApi.reviewHistory = function () {
            return reviewHistoryApi.get({});
        };

        appReviewApi.approve = function (appReviewId, notes) {
            return approveApi.save({appReviewId: appReviewId}, notes);
        };

        appReviewApi.disapprove = function (appReviewId, notes) {
            return disapproveApi.save({appReviewId: appReviewId}, notes);
        };

        appReviewApi.parseAppReviewResponse = function (response) {
            return response.result;
        };

        return appReviewApi;
    }
]);