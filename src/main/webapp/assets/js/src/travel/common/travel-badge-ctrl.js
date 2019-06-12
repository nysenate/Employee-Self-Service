var essTravel = angular.module('essTravel');

essTravel.controller('TravelBadgeCtrl', ['$scope', 'badgeService', 'ApplicationReviewApi', travelBadgeCtrl]);

function travelBadgeCtrl($scope, badgeService, appReviewApi) {

    const travelBadgeId = "travelPendingAppReviewCount";

    var badgeResource = undefined;

    // Update the app review badge count on page changes.
    $scope.$on('$locationChangeSuccess', function(event, newUrl, oldUrl) {
        badgeResource = appReviewApi.pendingReviews();
        badgeResource.$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                badgeService.setBadgeValue(travelBadgeId, appReviews.length);
            })
            .catch(function (error) {
                console.log("Error loading travel pending application reviews badge." + error);
            });
    });

    // Cancel the badgeResource request if it has not yet completed when leaving the page.
    $scope.$on('$locationChangeStart', function (event, next, current) {
        if (badgeResource && badgeResource.success !== true) {
            badgeResource.$cancelRequest();
        }
    })
}