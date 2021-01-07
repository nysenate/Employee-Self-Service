var essTravel = angular.module('essTravel');

essTravel.controller('ReconcileTravelCtrl', ['$scope', 'LocationService', 'modals', 'ApplicationReviewApi', reconcileTravel]);

function reconcileTravel($scope, locationService, modals, appReviewApi) {

    const DATEPICKER_FORMAT = "MM/DD/YYYY";
    const ISO_FORMAT = "YYYY-MM-DD";

    var vm = this;

    vm.data = {
        isLoading: true,
        reviews: {
            all: [],
            filtered: []
        },
        travelers: {
            all: [],
            selected: {}
        }
    }

    vm.date = {
        from: moment().subtract(3, 'month').format(DATEPICKER_FORMAT),
        to: moment().add(3, 'month').format(DATEPICKER_FORMAT)
    };

    (function () {
        appReviewApi.reconcileReviews().$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                vm.data.reviews.all = appReviews;
                vm.onFilterChange();
                vm.data.isLoading = false;
            })
            .catch($scope.handleErrorResponse);
    })();

    vm.displayReviewViewModal = function (review) {
        modals.open("app-review-view-modal", review, true);
    };

    vm.onFilterChange = function () {
        vm.data.reviews.filtered = angular.copy(vm.data.reviews.all);

        // Filter by date
        vm.data.reviews.filtered = vm.data.reviews.filtered.filter(function (r) {
            return moment(r.travelApplication.activeAmendment.startDate, ISO_FORMAT) >= moment(vm.date.from, DATEPICKER_FORMAT) &&
                moment(r.travelApplication.activeAmendment.startDate, ISO_FORMAT) <= moment(vm.date.to, DATEPICKER_FORMAT);
        });

        // Sort by date
        vm.data.reviews.filtered.sort(function (a, b) {
            return moment(a.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x')
                - moment(b.travelApplication.activeAmendment.startDate, ISO_FORMAT).format('x');
        })

        // Set travelers to unique travlers in filtered reviews.
        vm.setTravelers(vm.data.reviews.filtered);

        // If there is a selected traveler, filter for only their app reviews.
        if (!_.isEmpty(vm.data.travelers.selected)) {
            vm.data.reviews.filtered = vm.data.reviews.filtered.filter(function (review) {
                return review.travelApplication.traveler.employeeId === vm.data.travelers.selected.employeeId;
            });
        }
    }

    vm.setTravelers = function (reviews) {
        vm.data.travelers.all = [];
        reviews.forEach(function (r) {
            vm.data.travelers.all.push(r.travelApplication.traveler);
        })
        vm.data.travelers.all = _.uniq(vm.data.travelers.all, function (t) {
            return t.employeeId;
        });
    }
}