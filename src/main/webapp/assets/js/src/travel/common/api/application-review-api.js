angular.module('essTravel').factory('ApplicationReviewApi', [
    '$resource', 'appProps', 'modals', 'RestErrorService',
    function ($resource, appProps, modals, restErrorService) {

        var pendingReviewsApi = $resource(appProps.apiPath + '/travel/review/pending',
                                          {},
                                          {get: {method: 'GET', cancellable: true}});
        var reviewHistoryApi = $resource(appProps.apiPath + '/travel/review/history',
                                         {},
                                         {get: {method: 'GET', cancellable: true}});
        var approveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/approve',
                                   {appReviewId: '@appReviewId'},
                                   {save: {method: 'POST', cancellable: true}});
        var disapproveApi = $resource(appProps.apiPath + '/travel/review/:appReviewId/disapprove',
                                      {appReviewId: '@appReviewId'},
                                      {save: {method: 'POST', cancellable: true}});
        var editReviewApi = $resource(appProps.apiPath + '/travel/review/:appReviewId',
                                      {appReviewId: '@appReviewId'},
                                      {save: {method: 'POST', cancellable: true}});
        var sharedReviewApi = $resource(appProps.apiPath + '/travel/review/shared',
                                        {},
                                        {get: {method: 'GET', cancellable: true}});
        var reconcileReviewApi = $resource(appProps.apiPath + '/travel/review/reconcile');

        var appReviewApi = {};

        function ActionBody(notes) {
            this.notes = notes;
        }

        /**
         * Get all application reviews which need to be reviewed by the logged in user.
         * Will get all application reviews pending review by a role in roles array.
         */
        appReviewApi.pendingReviews = function () {
            return pendingReviewsApi.get({});
        };

        appReviewApi.reviewHistory = function () {
            return reviewHistoryApi.get({});
        };

        appReviewApi.approve = function (appReviewId, role, notes) {
            return approveApi.save({appReviewId: appReviewId, role: role}, new ActionBody(notes));
        };

        appReviewApi.disapprove = function (appReviewId, role, notes) {
            return disapproveApi.save({appReviewId: appReviewId, role: role}, new ActionBody(notes));
        };

        appReviewApi.editReview = function (appReviewId, isShared) {
            return editReviewApi.save({appReviewId: appReviewId, isShared: isShared}, {});
        }

        appReviewApi.parseAppReviewResponse = function (response) {
            return response.result;
        };

        appReviewApi.sharedReviews = function () {
            return sharedReviewApi.get({});
        }

        appReviewApi.reconcileReviews = function (from, to) {
            return reconcileReviewApi.get({from: from, to: to});
        }

        return appReviewApi;
    }
]);