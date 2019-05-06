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

    $scope.displayAppReviewViewModal = function (app) {
        var appReview;
        $scope.data.appReviews.forEach(function (ar) {
            if (app.id === ar.travelApplication.id) {
                appReview = ar;
            }
        });
        modals.open("app-review-view-modal", appReview, true);
    }
}