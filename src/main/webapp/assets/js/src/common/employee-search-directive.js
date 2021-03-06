angular.module('ess').directive('employeeSearch', [
    'appProps', 'modals', 'LocationService', 'EmployeeSearchApi', 'EmpInfoApi', 'PaginationModel', 'RestErrorService',
    function (appProps, modals, locationService, employeeSearchApi, empInfoApi, paginationModel, RestErrorService) {
        return {
            scope: {
                selectedEmp: '=?'
            },
            restrict: 'E',
            templateUrl: appProps.ctxPath + '/template/time/personnel/employee-search-directive',
            link: function ($scope, $elem, $attrs) {
                var EMP_ID_PARAM = 'empId';
                var TERM_PARAM = 'term';
                var ACTIVE_ONLY_PARAM = 'activeOnly';

                $scope.selectedEmp = null;
                $scope.empInfo = null;
                $scope.activeOnly = locationService.getSearchParam(ACTIVE_ONLY_PARAM) === 'true';
                $scope.searchTerm = locationService.getSearchParam(TERM_PARAM) || "";
                $scope.searchResults = [];

                var empId = parseInt(locationService.getSearchParam(EMP_ID_PARAM) || NaN);

                var pagination = angular.copy(paginationModel);
                pagination.itemsPerPage = 50;

                /* --- Watches --- */

                /** Perform a new search when the search term changes */
                $scope.$watchGroup(['searchTerm', 'activeOnly'], newSearch);

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
                    locationService.setSearchParam(EMP_ID_PARAM, emp.empId);
                };

                /**
                 * Deselects the currently selected employee
                 */
                $scope.clearSelectedEmp = function () {
                    $scope.selectedEmp = null;
                    $scope.empInfo = null;
                    if (empId) {
                        clearEmpId();
                        newSearch();
                    }
                    clearEmpId();
                };

                /* --- Api Methods --- */

                function getSearchResults() {
                    $scope.loadingEmps = true;
                    var params = {
                        term: $scope.searchTerm,
                        empId: validEmpId() ? empId : 0,
                        activeOnly: $scope.activeOnly,
                        limit: pagination.getLimit(),
                        offset: pagination.getOffset()
                    };
                    return employeeSearchApi.get(params, onSuccess, $scope.handleErrorResponse)
                        .$promise.finally(function () {
                            $scope.loadingEmps = false;
                        });

                    function onSuccess(resp) {
                        console.log('Got employee search results');
                        resp.employees.forEach(function (emp) {
                            $scope.searchResults.push(emp);
                        });
                        pagination.setTotalItems(resp.total);

                        if (validEmpId() && $scope.searchResults.length > 0) {
                            $scope.selectEmp($scope.searchResults[0]);
                        } else {
                            clearEmpId();
                        }
                    }
                }

                function getEmpInfo() {
                    var params = {
                        empId: $scope.selectedEmp.empId,
                        detail: true
                    };
                    $scope.loadingEmpInfo = true;
                    return empInfoApi.get(params, onSuccess, RestErrorService.handleErrorResponse)
                        .$promise.finally(function () {
                            $scope.loadingEmpInfo = false;
                        });

                    function onSuccess(resp) {
                        console.log('Got employee info');
                        $scope.empInfo = resp.employee;
                    }
                }

                /* --- Internal Methods --- */

                /**
                 * Clears current search results, resets pagination, and performs a new search
                 */
                function newSearch() {
                    $scope.searchResults = [];
                    pagination.reset();
                    locationService.setSearchParam(TERM_PARAM, $scope.searchTerm, /\S/.test($scope.searchTerm));
                    locationService.setSearchParam(ACTIVE_ONLY_PARAM, $scope.activeOnly.toString(), $scope.activeOnly);
                    return getSearchResults();
                }

                function validEmpId() {
                    return !isNaN(parseInt(empId)) && empId > 0;
                }

                function clearEmpId() {
                    empId = NaN;
                    locationService.setSearchParam(EMP_ID_PARAM);
                }

            }
        };
    }]);