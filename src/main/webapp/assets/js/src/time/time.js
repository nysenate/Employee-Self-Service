var essTime = angular.module('essTime');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('TimeMainCtrl', ['$scope', 'appProps', 'LocationService', 'badgeService', 'modals', 'SupervisorTimeRecordCountsApi',
    function($scope, appProps, locationService, badgeService, modals, SupervisorTimeRecordCountsApi) {

        $scope.initializePendingRecordsBadge = function() {
            var isoDateFmt = 'YYYY-MM-DD';
            var params = {
                supId: appProps.user.employeeId,
                status: 'SUBMITTED',
                from: moment().subtract(1, 'year').format(isoDateFmt),
                to: moment().add(1, 'month').format(isoDateFmt)
            };
            SupervisorTimeRecordCountsApi.get(params, function(resp) {
                badgeService.setBadgeValue('pendingRecordCount', resp.result.count);
            });
        };

        $scope.go = function(path, params) {
            locationService.go(path, false, params);
        };

        $scope.logout = function() {
            locationService.go('/logout', true);
        };

        $scope.log = function(stuff) {
            console.log(stuff);
        };

        $scope.handleErrorResponse = function (resp) {
            console.error("Request error:", resp);
            modals.open('500', {details: resp});
        }
    }
]);