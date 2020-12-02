var essTime = angular.module('essTime');

/**
 * The wrapping controller that is the parent of the nav menu and view content.
 */
essApp.controller('TimeMainCtrl', ['$scope', 'appProps', 'LocationService', 'badgeService',
                                   'ApprovalSupervisorTimeOffRequestsApi',
                                   'SupervisorTimeRecordCountsApi',
                                   'EmployeeDateRangeApi',
                                   'TimeOffRequestListService',
    function($scope, appProps, locationService, badgeService, ApprovalSupervisorTimeOffRequestsApi, SupervisorTimeRecordCountsApi, EmployeeDateRangeApi, TimeOffRequestListService) {

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
            $scope.initializePendingRequestsBadge();
        };
        $scope.initializePendingRequestsBadge = function() {
            var params = {
                supId: appProps.user.employeeId
            };
            ApprovalSupervisorTimeOffRequestsApi.query(params, function(data) {
                badgeService.setBadgeValue('pendingRequestCount', data.length);
            });
        };
        $scope.initializeActiveRequestsBadge = function() {
            /* Begin API calls with the Active Request Call */
            EmployeeDateRangeApi.query({
                empId: appProps.user.employeeId,
                startRange: new Date().toISOString().substr(0, 10),
                endRange: "" //Will be changed to DateUtils.THE_FUTURE in Ctrl
            }).$promise.then(function (data) {
                var requests = TimeOffRequestListService.formatData(data);
                var pendingRequestCount = 0;
                var approvedRequestCount = 0;
                var rejectedRequestCount = 0;
                for (var index = 0; index < requests.length; ++index) {
                    if (requests[index].status === "SUBMITTED") pendingRequestCount++;
                    if (requests[index].status === "APPROVED") approvedRequestCount++;
                    if (requests[index].status === "DISAPPROVED") rejectedRequestCount++;
                }
                badgeService.setBadgeValue("activeRequestCount", pendingRequestCount);
                badgeService.setBadgeValue("activeApprovedRequestCount", approvedRequestCount);
                badgeService.setBadgeValue("activeRejectedRequestCount", rejectedRequestCount);
            })
            .catch(console.error);
        }
    }
]);