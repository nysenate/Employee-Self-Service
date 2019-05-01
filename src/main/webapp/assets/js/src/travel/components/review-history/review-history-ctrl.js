var essTravel = angular.module('essTravel');

essTravel.controller('ReviewHistoryCtrl', ['$scope', 'modals', 'ApplicationReviewApi', reviewHistory]);

function reviewHistory($scope, modals, appReviewApi) {

    this.$onInit = function () {
        $scope.data = {
            isLoading: true,
            appReviews: [],
            apps: []
        };

        appReviewApi.reviewHistory()
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    $scope.data.appReviews.push(review);
                    $scope.data.apps.push(review.travelApplication);
                });
                $scope.data.isLoading = false;
            })
    };

    $scope.displayAppFormViewModal = function (app) {
        modals.open("app-form-view-modal", app, true);
    }
}