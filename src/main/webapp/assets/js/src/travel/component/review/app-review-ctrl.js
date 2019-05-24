var essTravel = angular.module('essTravel');

essTravel.controller('AppReviewCtrl', ['$scope', 'modals', 'LocationService', 'ApplicationReviewApi', reviewController]);

function reviewController($scope, modals, locationService, appReviewApi) {

    var vm = this;
    vm.isLoading = true;
    vm.apps = [];
    vm.appReviews = [];

    (function init() {
        getPendingAppReviews();
    })();

    function getPendingAppReviews() {
        appReviewApi.pendingReviews()
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.appReviews.push(review);
                    vm.apps.push(review.travelApplication);
                });
                vm.isLoading = false;
            });
    }

    vm.displayReviewFormModal = function (app) {
        var appReview = {};
        vm.appReviews.forEach(function (a) {
            if (a.travelApplication.id === app.id) {
                appReview = a;
            }
        });
        modals.open("app-review-action-modal", appReview, true)
            .then(function () {
                locationService.go("/travel/review", true);
            });
    };
}
