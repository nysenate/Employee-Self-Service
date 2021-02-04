var essTravel = angular.module('essTravel');

essTravel.controller('TravelBadgeCtrl', ['$scope', 'badgeService', 'ApplicationReviewApi', 'TravelRoleService', travelBadgeCtrl]);

function travelBadgeCtrl($scope, badgeService, appReviewApi, roleService) {

    const travelBadgeId = "travelPendingAppReviewCount";
    var badgeResource = undefined;
    var roles = [];

    (function init() {
        roleService.roles()
            .then(function (response) {
                roles = response.roles.map(function (role) {
                    return role.name;
                });
                // Ensure the badge has been updated after we have the correct roles.
                updateBadge();
            });
    })();

    // Update the app review badge count on page changes.
    $scope.$on('$locationChangeSuccess', function (event, newUrl, oldUrl) {
        updateBadge();
    });

    function updateBadge() {
        badgeResource = appReviewApi.pendingReviews(roles);
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
        if (badgeResource && badgeResource.success !== true) {
            badgeResource.$cancelRequest();
        }
    })
}