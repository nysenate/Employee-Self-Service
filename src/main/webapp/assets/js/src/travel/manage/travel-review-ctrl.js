var essTravel = angular.module('essTravel');

essTravel.controller('TravelReviewCtrl', ['$scope', 'TravelApplicationReviewApi', reviewController]);

function reviewController($scope, reviewApi) {

    this.$onInit = function () {
        $scope.data = {
            apps: []
        }
    };

    reviewApi.get({}, function (response) {
        response.result.forEach(function (result) {
            $scope.data.apps.push(result.travelApplication);
        });
        console.log($scope.data.apps);
    })
}