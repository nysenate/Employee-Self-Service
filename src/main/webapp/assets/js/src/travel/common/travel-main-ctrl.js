var essTravel = angular.module('essTravel');

essTravel.controller('TravelMainCtrl', ['$scope', 'badgeService', travelMainCtrl]);

function travelMainCtrl($scope, badgeService) {

    const travelBadgeId = "travelPendingAppReviewCount";

    $scope.$on('$locationChangeSuccess', function(event, newUrl, oldUrl) {
        console.log(newUrl);
        badgeService.setBadgeValue(travelBadgeId, 5);
    })
}