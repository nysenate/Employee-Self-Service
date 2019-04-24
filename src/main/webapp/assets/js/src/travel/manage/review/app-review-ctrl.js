var essTravel = angular.module('essTravel');

essTravel.controller('AppReviewCtrl', ['$scope', 'modals', 'LocationService', 'TravelApplicationApprovalApi', reviewController]);

function reviewController($scope, modals, locationService, appApprovalApi) {

    this.$onInit = function () {
        $scope.data = {
            apiRequest: {},
            apps: [],
            approvals: []
        };

        initData();
    };

    function initData() {
        $scope.data.apiRequest = appApprovalApi.get({}, function (response) {
            response.result.forEach(function (result) {
                $scope.data.approvals.push(result);
                $scope.data.apps.push(result.travelApplication);
            });
            sortByTravelDateAsc($scope.data.apps);
        })
    }

    // Duplicated in travel-view-applications
    function sortByTravelDateAsc(apps) {
        apps.sort(function(a, b) {
            // Turn your strings into dates, and then subtract them
            // to get a value that is either negative, positive, or zero.
            return new Date(b.startDate) - new Date(a.startDate);
        });
    }

    $scope.viewApplicationForm = function (app) {
        var appApproval = {};
        $scope.data.approvals.forEach(function (a) {
            if (a.travelApplication.id === app.id) {
                appApproval = a;
            }
        });
        modals.open("app-review-form-modal", appApproval, true)
            .then(function () {
                locationService.go("/travel/manage/review", true);
            });
    }
}
