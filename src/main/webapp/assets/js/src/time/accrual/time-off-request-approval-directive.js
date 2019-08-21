(function () {

    var essTime = angular.module('essTime');

    essTime.directive('timeOffRequestApproval', ['appProps', 'supEmpGroupService', requestApprovalDirective]);

    function requestApprovalDirective(appProps, supEmpGroupService) {
        return {
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-approval-directive',
            transclude: true,
            scope: {
                approve: '=',
                active: '='
            },
            link: function($scope) {

                $scope.loadingEmployees = true;
                supEmpGroupService.init().finally(function () {
                    $scope.loadingEmployees = false;
                    $scope.changeToNames();
                });

                /**
                 * Function that adds a 'name' attribute to each request.
                 * The name will be the first and last name of the employee
                 * whose employeeId is on the request
                 */
                $scope.changeToNames = function() {
                    if(!$scope.loadingEmployees) {
                        //change all the empIds to names
                        $scope.approve.forEach(function(request) {
                            request.name = supEmpGroupService.getName(request.empId).firstName
                            + " " + supEmpGroupService.getName(request.empId).lastName;
                        });
                        $scope.active.forEach(function(request) {
                            request.name = supEmpGroupService.getName(request.empId).firstName
                                + " " + supEmpGroupService.getName(request.empId).lastName;
                        });
                    }
                }
            }
        }
    }

})();