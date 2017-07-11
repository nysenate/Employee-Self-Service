angular.module('essTime')
    .directive('employeeSearch', ['appProps', 'modals', 'EmployeeSearchApi', 'EmpInfoApi', 'PaginationModel',
                                  employeeSearchDirective]);

function employeeSearchDirective(appProps, modals, employeeSearchApi, empInfoApi, paginationModel) {
    return {
        scope: {
            selectedEmp: '=?'
        },
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/time/personnel/employee-search-directive',
        link: function ($scope, $elem, $attrs) {
            $scope.selectedEmp = null;
            $scope.empInfo = null;
            $scope.searchTerm = "";
            $scope.searchResults = [];

            var pagination = angular.copy(paginationModel);
            pagination.itemsPerPage = 50;

            /* --- Watches --- */

            /** Perform a new search when the search term changes */
            $scope.$watch('searchTerm', newSearch);

            /* --- Display Methods --- */

            $scope.searchResultsExist = function () {
                return $scope.searchResults && $scope.searchResults.length > 0;
            };

            /**
             * Expands the result window
             */
            $scope.getNextSearchResults = function () {
                if ($scope.loadingEmps || pagination.onLastPage()) {
                    return;
                }
                pagination.nextPage();
                return getSearchResults();
            };

            /**
             * Sets the given employee as the selected employee
             * @param emp
             */
            $scope.selectEmp = function (emp) {
                $scope.selectedEmp = emp;
                getEmpInfo();
            };

            /**
             * Deselects the currently selected employee
             */
            $scope.clearSelectedEmp = function () {
                $scope.selectedEmp = null;
                $scope.empInfo = null;
            };

            /* --- Api Methods --- */

            function getSearchResults() {
                $scope.loadingEmps = true;
                var params = {
                    term: $scope.searchTerm,
                    limit: pagination.getLimit(),
                    offset: pagination.getOffset()
                };
                return employeeSearchApi.get(params, onSuccess, onFail)
                    .$promise.finally(function () {
                        $scope.loadingEmps = false;
                    });
                function onSuccess(resp) {
                    console.log('Got employee search results');
                    resp.employees.forEach(function (emp) {
                        $scope.searchResults.push(emp);
                    });
                    pagination.setTotalItems(resp.total);
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

            /**
             * Clears current search results, resets pagination, and performs a new search
             */
            function newSearch() {
                $scope.searchResults = [];
                pagination.reset();
                return getSearchResults();
            }

        }
    };
}