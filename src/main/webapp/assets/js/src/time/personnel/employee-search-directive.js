angular.module('essTime')
    .directive('employeeSearch', ['$filter', 'appProps', 'modals', 'ActiveEmployeeApi', employeeSearchDirective]);

function employeeSearchDirective($filter,appProps, modals, activeEmpApi) {
    return {
        scope: {
            selectedEmp: '=?'
        },
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/time/personnel/employee-search-directive',
        link: function ($scope, $elem, $attrs) {
            $scope.activeEmps = null;
            $scope.selectedEmp = null;
            $scope.search = {
                fullName: ""
            };

            getActiveEmps();

            /* --- Display Methods --- */

            $scope.selectEmp = function (emp) {
                $scope.selectedEmp = emp;
            };

            $scope.clearSelectedEmp = function () {
                $scope.selectedEmp = null;
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

            /* --- Internal Methods --- */

            function sortActiveEmps() {
                $scope.activeEmps = $filter('orderBy')($scope.activeEmps, ['lastName', 'firstName']);
            }
        }
    };
}