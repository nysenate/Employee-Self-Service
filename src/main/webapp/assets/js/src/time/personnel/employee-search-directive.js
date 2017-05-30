angular.module('essTime')
    .directive('employeeSearch', ['$filter', 'appProps', 'modals', 'ActiveEmployeeApi', 'EmpInfoApi',
                                  employeeSearchDirective]);

function employeeSearchDirective($filter,appProps, modals, activeEmpApi, empInfoApi) {
    return {
        scope: {
            selectedEmp: '=?'
        },
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/time/personnel/employee-search-directive',
        link: function ($scope, $elem, $attrs) {
            $scope.activeEmps = null;
            $scope.selectedEmp = null;
            $scope.empInfo = null;
            $scope.search = {
                fullName: ""
            };

            getActiveEmps();

            /* --- Display Methods --- */

            $scope.selectEmp = function (emp) {
                $scope.selectedEmp = emp;
                getEmpInfo();
            };

            $scope.clearSelectedEmp = function () {
                $scope.selectedEmp = null;
                $scope.empInfo = null;
            };

            /* --- Api Methods --- */

            function getActiveEmps() {
                $scope.loadingEmps = true;
                return activeEmpApi.get({}, onSuccess, onFail)
                    .$promise.finally(function () {
                        $scope.loadingEmps = false;
                    });
                function onSuccess(resp) {
                    console.log('Got employee list');
                    $scope.activeEmps = resp.employees;
                    sortActiveEmps()
                }
                function onFail(resp) {
                    console.error('Failed to get active employees', resp);
                    modals.open('500', {details: resp});
                }
            }

            function getEmpInfo() {
                var params = {
                    empId: $scope.selectedEmp.empId,
                    detail: true
                };
                $scope.loadingEmpInfo = true;
                return empInfoApi.get(params, onSuccess, onFail)
                    .$promise.finally(function () {
                        $scope.loadingEmpInfo = false;
                    });
                function onSuccess(resp) {
                    console.log('Got employee info');
                    $scope.empInfo = resp.employee;
                }
                function onFail(resp) {
                    console.error('Failed to get employee info', resp);
                    modals.open('500', {details: resp});
                }
            }

            /* --- Internal Methods --- */

            function sortActiveEmps() {
                $scope.activeEmps = $filter('orderBy')($scope.activeEmps, ['lastName', 'firstName']);
            }
        }
    };
}