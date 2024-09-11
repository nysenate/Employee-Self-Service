var essTravel = angular.module('essTravel');

essTravel.controller('TravelBadgeCtrl', ['$scope', 'badgeService', 'ApplicationReviewApi', 'TravelRoleService', travelBadgeCtrl]);

function travelBadgeCtrl($scope, badgeService, appReviewApi) {

    const deptHeadBadgeName = "travelPendingDeptHdCount";
    const adminBadgeName = "travelPendingAdminCount";
    const secretaryBadgeName = "travelPendingSecretaryCount"
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
                var deptHdCount = 0;
                var adminCount = 0;
                var secretaryCount = 0;
                var reviewsByRole = appReviews.items;
                for (var role in reviewsByRole) {
                    if (reviewsByRole.hasOwnProperty(role)) {
                        switch(role) {
                            case "DEPARTMENT_HEAD":
                                deptHdCount += reviewsByRole[role].length;
                                break;
                            case "TRAVEL_ADMIN":
                                adminCount += reviewsByRole[role].length;
                                break;
                            case "SECRETARY_OF_THE_SENATE":
                                secretaryCount += reviewsByRole[role].length;
                                break;
                        }
                    }
                }
                badgeService.setBadgeValue(deptHeadBadgeName, deptHdCount);
                badgeService.setBadgeValue(adminBadgeName, adminCount);
                badgeService.setBadgeValue(secretaryBadgeName, secretaryCount);
            })
            .catch(function (error) {
                console.error("Error loading travel pending application reviews badge." + error);
            });
    }

    // Cancel the badgeResource request if it has not yet completed when leaving the page.
    $scope.$on('$locationChangeStart', function (event, next, current) {
        if (badgeResource && badgeResource.$resolved !== true) {
            badgeResource.$cancelRequest();
        }
    })
}