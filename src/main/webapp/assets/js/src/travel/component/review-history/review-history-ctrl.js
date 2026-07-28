var essTravel = angular.module('essTravel');

essTravel.controller('ReviewHistoryCtrl', ['$scope', 'LocationService', 'modals', 'ApplicationReviewApi',
                                           'TravelRoleService', reviewHistory]);

function reviewHistory($scope, locationService, modals, appReviewApi, roleService) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;
    vm.data = {
        isLoading: true,
        reviews: {
            all: [],
            filtered: []
        },
        roles: []
    };
    vm.date = {
        from: moment().subtract(3, 'month').format(DATEPICKER_FORMAT),
        to: moment().add(3, 'month').format(DATEPICKER_FORMAT)
    };

    (function () {
        appReviewApi.reviewHistory()
            .$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                appReviews.forEach(function (review) {
                    vm.data.reviews.all.push(review);
                });
                vm.applyFilters();
                vm.data.isLoading = false;
            })
            .catch($scope.handleErrorResponse);

        roleService.roles()
            .then(function (response) {
                vm.data.roles = response.allRoles;
            });
    })();

    vm.displayReviewViewModal = function (review) {
        modals.open("app-review-view-modal", review, true);
    };

    // Called by the app-review-view-modal.
    vm.onEdit = function (review) {
        modals.reject();
        locationService.go("/travel/application/edit", true, {appId: review.travelApplication.id, role: 'TRAVEL_ADMIN'});
    };

    vm.applyFilters = function () {
        vm.data.reviews.filtered = angular.copy(vm.data.reviews.all);
        vm.data.reviews.filtered = vm.data.reviews.filtered.filter(function (r) {
            return moment(r.travelApplication.activeAmendment.startDate, ISO_FORMAT) >= moment(vm.date.from, DATEPICKER_FORMAT) &&
                moment(r.travelApplication.activeAmendment.startDate, ISO_FORMAT) <= moment(vm.date.to, DATEPICKER_FORMAT);
        });
        vm.data.reviews.filtered.sort(function(a, b) {
            return moment(a.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x')
                - moment(b.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x');
        })
    };
}
