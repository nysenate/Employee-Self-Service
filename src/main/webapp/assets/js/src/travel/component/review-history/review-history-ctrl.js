var essTravel = angular.module('essTravel');

essTravel.controller('ReviewHistoryCtrl', ['$scope', 'LocationService', 'modals', 'ApplicationReviewApi', reviewHistory]);

function reviewHistory($scope, locationService, modals, appReviewApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;
    vm.data = {
        isLoading: true,
        appReviews: [],
        apps: {
            all: [],
            filtered: []
        }
    };
    vm.date = {
        from: moment().subtract(3, 'month').format(DATEPICKER_FORMAT),
        to: moment().add(3, 'month').format(DATEPICKER_FORMAT)
    };

    (function () {
        appReviewApi.reviewHistory()
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.data.appReviews.push(review);
                    vm.data.apps.all.push(review.travelApplication);
                });
                vm.applyFilters();
                vm.data.isLoading = false;
            });
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
    };

    vm.applyFilters = function () {
        vm.data.apps.filtered = angular.copy(vm.data.apps.all);
        vm.data.apps.filtered = vm.data.apps.filtered.filter(function (app) {
            return moment(app.startDate, ISO_FORMAT) >= moment(vm.date.from, DATEPICKER_FORMAT) &&
                moment(app.startDate, ISO_FORMAT) <= moment(vm.date.to, DATEPICKER_FORMAT);
        });
    };
}