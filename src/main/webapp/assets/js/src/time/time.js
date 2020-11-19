var essTime = angular.module('essTime');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('TimeMainCtrl', ['$scope', 'appProps', 'LocationService', 'badgeService',
                                   'ApprovalSupervisorTimeOffRequestsApi', 'SupervisorTimeRecordCountsApi',
    function($scope, appProps, locationService, badgeService, ApprovalSupervisorTimeOffRequestsApi, SupervisorTimeRecordCountsApi) {

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
        $scope.initializePendingRequestsBadge = function() {
            var params = {supId: appProps.user.employeeId};
            ApprovalSupervisorTimeOffRequestsApi.query(params, function(data) {
               badgeService.setBadgeValue('pendingRequestCount', data.count);
            });
        };
    }
]);