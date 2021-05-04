var essTravel = angular.module('essTravel');

essTravel.controller('TravelBadgeCtrl', ['$scope', 'badgeService', 'ApplicationReviewApi', 'TravelRoleService', travelBadgeCtrl]);

function travelBadgeCtrl($scope, badgeService, appReviewApi) {

    const travelBadgeId = "travelPendingAppReviewCount";
    var badgeResource = undefined;

    (function init() {
        updateBadge();
    })();

    // Update the app review badge count on page changes.
    $scope.$on('$locationChangeSuccess', function (event, newUrl, oldUrl) {
        updateBadge();
    });

    function updateBadge() {
        badgeResource = appReviewApi.pendingReviews();
        badgeResource.$promise
            .then(appReviewApi.parseAppReviewResponse)
            .then(function (appReviews) {
                var count = 0;
                var reviewsByRole = appReviews.items;
                for (var role in reviewsByRole) {
                    if (reviewsByRole.hasOwnProperty(role)) {
                        count += reviewsByRole[role].length;
                    }
                }
                badgeService.setBadgeValue(travelBadgeId, count);
            })
            .catch(function (error) {
                console.log("Error loading travel pending application reviews badge." + error);
            });
    }

    // Cancel the badgeResource request if it has not yet completed when leaving the page.
    $scope.$on('$locationChangeStart', function (event, next, current) {
        if (badgeResource && badgeResource.$resolved !== true) {
            badgeResource.$cancelRequest();
        }
    })
}