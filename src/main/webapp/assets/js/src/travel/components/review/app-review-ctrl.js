var essTravel = angular.module('essTravel');

essTravel.controller('AppReviewCtrl', ['$scope', 'modals', 'LocationService', 'ApplicationReviewApi', reviewController]);

function reviewController($scope, modals, locationService, appReviewApi) {

    this.$onInit = function () {
        $scope.data = {
            isLoading: true,
            apps: [],
            appReviews: []
        };

        initData();
    };

    function initData() {
        appReviewApi.pendingReviews()
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    $scope.data.appReviews.push(review);
                    $scope.data.apps.push(review.travelApplication);
                });

                $scope.data.isLoading = false;
            });
    }

    $scope.displayReviewFormModal = function (app) {
        var appReview = {};
        $scope.data.appReviews.forEach(function (a) {
            if (a.travelApplication.id === app.id) {
                appReview = a;
            }
        });
        modals.open("app-review-form-modal", appReview, true)
            .then(function () {
                locationService.go("/travel/review", true);
            });
    }
}
