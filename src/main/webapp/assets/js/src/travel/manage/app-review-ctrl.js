var essTravel = angular.module('essTravel');

essTravel.controller('AppReviewCtrl', ['$scope', 'modals', 'TravelApplicationReviewApi', reviewController]);

function reviewController($scope, modals, reviewApi) {

    this.$onInit = function () {
        $scope.data = {
            apiRequest: {},
            apps: []
        };

        initData();
    };

    function initData() {
        $scope.data.apiRequest = reviewApi.get({}, function (response) {
            response.result.forEach(function (result) {
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
        modals.open("app-form-view-modal", app, true);
    }
}