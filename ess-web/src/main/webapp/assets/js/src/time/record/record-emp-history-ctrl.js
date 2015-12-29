var essTime = angular.module('essTime');

essTime.controller('EmpRecordHistoryCtrl', ['$scope', 'appProps',  'ActiveYearsTimeRecordsApi', 'TimeRecordsApi',
                                            'SupervisorEmployeesApi', 'modals', 'RecordUtils',
    function ($scope, appProps, ActiveYearsTimeRecordsApi, TimeRecordsApi, SupervisorEmployeesApi, modals, recordUtils) {

        $scope.state = {
            supId: appProps.user.employeeId,
            searching: false,
            todayMoment: moment(),

            selectedEmp: null,
            recordYears: [],
            selectedRecYear: null,
            records: [],

            allEmps: [],
            primaryEmps: []
        };

        $scope.getEmployeeGroups = function(supId, fromDate, toDate) {
            var fromDateMoment = (fromDate) ? moment(fromDate) : moment().subtract(2, 'years');
            var toDateMoment = (toDate) ? moment(toDate) : moment();
            $scope.state.searching = true;
            SupervisorEmployeesApi.get({
                supId: supId,
                fromDate: fromDateMoment.format('YYYY-MM-DD'),
                toDate: toDateMoment.format('YYYY-MM-DD')
            }, function(resp) {
                if (resp.success == true) {
                    $scope.state.primaryEmps = resp.result.primaryEmployees.sort(function(a,b) {
                        return a.empLastName.localeCompare(b.empLastName)});
                    // This lookup table maps empId -> last name in case it's needed for the supervisor overrides.
                    var primaryEmpLookup = {};
                    // Add all the employees into a single collection to populate the drop down.
                    angular.forEach($scope.state.primaryEmps, function(emp) {
                        emp.group = 'Direct employees';
                        primaryEmpLookup[emp.empId] = emp.empLastName;
                        setAdditionalEmpData(emp);
                        $scope.state.allEmps.push(emp);
                    });
                    angular.forEach(resp.result.empOverrideEmployees, function(emp) {
                        emp.group = 'Additional Employees';
                        setAdditionalEmpData(emp);
                        $scope.state.allEmps.push(emp);
                    });
                    angular.forEach(resp.result.supOverrideEmployees.items, function(supGroup, supId) {
                        angular.forEach(supGroup, function(emp) {
                            emp.group = ((primaryEmpLookup[supId]) ? primaryEmpLookup[supId] + '\'s Employees'
                                                                   : 'Sup Override Employees');
                            setAdditionalEmpData(emp);
                            $scope.state.allEmps.push(emp);
                        });
                    });
                    $scope.state.selectedEmp = $scope.state.allEmps[0];
                    $scope.getTimeRecordsForEmp($scope.state.selectedEmp);
                }
                $scope.state.searching = false;
            }, function(resp) {
                $scope.state.searching = false;
                modals.open('500', {details: resp});
                console.log(resp);
            });
        };

        function setAdditionalEmpData(emp) {
            emp.supStartMoment = moment(emp.supStartDate);
            emp.supEndMoment = (emp.supEndDate) ? moment(emp.supEndDate) : moment();
            emp.dropDownLabel = emp.empLastName + ' (' + emp.supStartMoment.format('MMM YYYY') + ' - ' +
                emp.supEndMoment.format('MMM YYYY') + ')';
        }

        $scope.getTimeRecordsForEmp = function(emp) {
            ActiveYearsTimeRecordsApi.get({empId: emp.empId}, function(resp) {
                if (resp.success) {
                    $scope.state.recordYears = resp.years.reverse();
                    $scope.state.selectedRecYear = $scope.state.recordYears[0];
                    if (resp.years.length > 0) {
                        $scope.getTimeRecordForEmpByYear(emp, $scope.state.selectedRecYear);
                    }
                }
            }, function(resp) {
                modals.open('500', {details: resp});
                console.log(resp);
            });
        };

        $scope.getTimeRecordForEmpByYear = function(emp, year) {
            var startMoment = moment([year, 0, 1]);
            var endMoment = ($scope.state.todayMoment.year() == year) ? moment() : moment([year, 11, 31]);
            $scope.state.searching = true;
            TimeRecordsApi.get({empId: emp.empId,
                                from: startMoment.format('YYYY-MM-DD'),
                                to: endMoment.format('YYYY-MM-DD')},
                function(resp) {
                    if (resp.success) {
                        $scope.state.records = resp.result.items[emp.empId].reverse();
                        for(var i in $scope.state.records) {
                            var record = $scope.state.records[i];
                            recordUtils.calculateDailyTotals(record);
                            record.totals = recordUtils.getRecordTotals(record);
                        }
                    }
                    $scope.state.searching = false;
                }, function(resp) {
                    modals.open('500', {details: resp});
                    console.log(resp);
                    $scope.state.searching = false;
                });
        };

        // Open a new modal window showing a detailed view of the given record
        $scope.showDetails = function(record) {
            var params = { record: record };
            modals.open('details', params);
        };

        $scope.init = function() {
            $scope.getEmployeeGroups($scope.state.supId);
        }();
    }]
);