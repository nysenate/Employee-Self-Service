(function () {

    var essTime = angular.module('essTime');

    essTime.directive('timeOffRequestApproval', ['appProps', 'supEmpGroupService', requestApprovalDirective]);

    function requestApprovalDirective(appProps, supEmpGroupService) {
        return {
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-approval-directive',
            transclude: true,
            scope: {
                requests: '=',
                format: '='
            },
            link: function($scope) {
                $scope.loadingEmployees = true;
                supEmpGroupService.init().finally(function () {
                    $scope.loadingEmployees = false;
                    $scope.changeToNames();
                    $scope.uncheckAll();
                });

                /**
                 * Function that adds a 'name' attribute to each request.
                 * The name will be the first and last name of the employee
                 * whose employeeId is on the request
                 */
                $scope.changeToNames = function() {
                    if(!$scope.loadingEmployees) {
                        //change all the empIds to names
                        $scope.requests.forEach(function(request) {
                            request.name = supEmpGroupService.getName(request.employeeId).firstName
                                + " " + supEmpGroupService.getName(request.employeeId).lastName;
                        });
                    }
                };

                /**
                 * Function that 'un-checks' all the requests. This adds the 'checked'
                 * attribute to each request, which will be bound to a checkbox in that request's
                 * row in the jsp
                 */
                $scope.uncheckAll = function () {
                    $scope.requests.forEach( function(request) {
                        request.checked = false;
                    });
                }
            }
        }
    }

})();