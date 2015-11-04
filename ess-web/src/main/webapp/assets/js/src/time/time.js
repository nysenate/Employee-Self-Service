var essTime = angular.module('essTime');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('TimeMainCtrl', ['$scope', 'appProps', 'LocationService', 'badgeService', 'SupervisorTimeRecordCountsApi',
    function($scope, appProps, locationService, badgeService, SupervisorTimeRecordCountsApi) {

        $scope.initializePendingRecordsBadge = function() {
            SupervisorTimeRecordCountsApi.get({supId: appProps.user.employeeId, status: 'SUBMITTED'}, function(resp) {
                badgeService.setBadgeValue('pendingRecordCount', resp.result.count);
            });
        };

        $scope.go = function(path, params) {
            locationService.go(path, false, params);
        };

        $scope.logout = function() {
            locationService.go('/logout', true);
        };
    }
]);