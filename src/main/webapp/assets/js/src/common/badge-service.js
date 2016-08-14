var essApp = angular.module('ess');

/**
 * Dead simple service for maintaining a collection of badge values.
 */
essApp.factory('badgeService', ['appProps', function(appProps) {
    var badgeService = {
        badges: {}       // Maps badge ids to their values
    };

    badgeService.setBadgeValue = function(badgeId, value) {
        badgeService.badges[badgeId] = value;
    };

    return badgeService;
}]);
