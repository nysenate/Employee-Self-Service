angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var pendingReviewsApi = $resource(appProps.apiPath + '/travel/review/pending.json',
                                          {roles: '@roles'},
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

        function ActionBody(notes, isDiscussionRequested) {
            this.notes = notes;
            this.isDiscussionRequested = isDiscussionRequested;
        }

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         * Will get all application reviews pending review by a role in roles array.
         */
        appReviewApi.pendingReviews = function (roles) {
            return pendingReviewsApi.get({roles: roles});
        };

        appReviewApi.reviewHistory = function () {
            return reviewHistoryApi.get({});
        };

        appReviewApi.approve = function (appReviewId, notes, isDiscussionRequested) {
            return approveApi.save({appReviewId: appReviewId}, new ActionBody(notes, isDiscussionRequested));
        };

        appReviewApi.disapprove = function (appReviewId, notes) {
            return disapproveApi.save({appReviewId: appReviewId}, new ActionBody(notes, false));
        };

        appReviewApi.parseAppReviewResponse = function (response) {
            return response.result;
        };

        return appReviewApi;
    }
]);