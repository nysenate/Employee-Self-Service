var essTravel = angular.module('essTravel');

essTravel.controller('ReviewHistoryCtrl', ['$scope', 'LocationService', 'modals', 'ApplicationReviewApi', reviewHistory]);

function reviewHistory($scope, locationService, modals, appReviewApi) {

    var vm = this;

    (function () {
        vm.data = {
            isLoading: true,
            appReviews: [],
            apps: []
        };

        appReviewApi.reviewHistory()
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.data.appReviews.push(review);
                    vm.data.apps.push(review.travelApplication);
                });
                vm.data.isLoading = false;
            })
    })();

    vm.displayAppReviewViewModal = function (app) {
        var appReview;
        vm.data.appReviews.forEach(function (ar) {
            if (app.id === ar.travelApplication.id) {
                appReview = ar;
            }
        });
        modals.open("app-review-view-modal", appReview, true);
    };

    vm.onEdit = function (appReview) {
        modals.reject();
        locationService.go("/travel/application/edit", true, {appId: appReview.travelApplication.id});
    }
}