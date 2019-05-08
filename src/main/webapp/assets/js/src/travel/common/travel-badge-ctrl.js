var essTravel = angular.module('essTravel');

essTravel.controller('TravelBadgeCtrl', ['$scope', 'badgeService', 'ApplicationReviewApi', travelBadgeCtrl]);

function travelBadgeCtrl($scope, badgeService, appReviewApi) {

    const travelBadgeId = "travelPendingAppReviewCount";

    // Update the app review badge count on page changes.
    $scope.$on('$locationChangeSuccess', function(event, newUrl, oldUrl) {
        appReviewApi.pendingReviews()
            .then(function (appReviews) {
                badgeService.setBadgeValue(travelBadgeId, appReviews.length);
            });
    })
}