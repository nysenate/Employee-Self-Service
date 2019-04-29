var essTravel = angular.module('essTravel');

essTravel.controller('AppReviewCtrl', ['$scope', 'modals', 'LocationService', 'TravelApplicationReviewApi', reviewController]);

function reviewController($scope, modals, locationService, appReviewApi) {

    this.$onInit = function () {
        $scope.data = {
            apiRequest: {},
            apps: [],
            appReviews: []
        };

        initData();
    };

    function initData() {
        $scope.data.apiRequest = appReviewApi.get({}, function (response) {
            response.result.forEach(function (result) {
                $scope.data.appReviews.push(result);
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
