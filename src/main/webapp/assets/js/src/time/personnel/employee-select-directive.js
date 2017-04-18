angular.module('essTime')
    .directive('employeeSelect', ['appProps', '$filter', 'supEmpGroupService', employeeSelectDirective]);

function employeeSelectDirective(appProps, $filter, supEmpGroupService) {
    return {
        scope: {
            selectedSup: "=?",
            selectedEmp: "=?"
        },
        restrict: 'E',
        templateUrl: appProps.ctxPath + '/template/time/personnel/employee-select',
        link: function ($scope, $elem, $attrs) {
            $scope.iSelEmpGroup = -1;
            $scope.iSelEmp = -1;
            $scope.supEmpGroups = [];
            $scope.allEmps = [];

            /* --- Attributes --- */

            /** If true, show only supervisors and employees that are currently active */
            $scope.activeOnly = ($attrs.activeOnly || '').toLowerCase() === 'true';

            /** If true, senators will appear in the employee dropdown.  Otherwise they are omitted */
            $scope.showSenators = ($attrs.showSenators || '').toLowerCase() === 'true';

            /**
             * Specifies the subject of the employee select label
             *  in the form "Select {selectSubject} for employee"
             */
            $scope.selectSubject = $attrs.selectSubject || 'info';

            /**
             * Wait for supervisor emp groups to load
             * then set local variables and label emp groups
             */
            supEmpGroupService.init().then(function () {
                $scope.iSelEmpGroup = 0;
                $scope.supEmpGroups = supEmpGroupService.getSupEmpGroupList();
                setSupGroupLabels();
            });

            /* --- Watches --- */

            $scope.$watch('iSelEmpGroup', setSelectedEmployeeGroup);

            $scope.$watch('iSelEmp', setSelectedEmployee);

            /* --- Display Methods --- */

            /**
             * Filter for the supervisor emp group select
             * @param empGroup
             * @returns {boolean}
             */
            $scope.supEmpGroupFilter = function (empGroup) {
                return activeFilter(empGroup);
            };

            /**
             * Filter for the employee select
             * @param empInfo
             * @returns {boolean}
             */
            $scope.employeeFilter = function (empInfo) {
                return activeFilter(empInfo) &&
                    senatorFilter(empInfo);
            };

            /* --- Internal Methods --- */

            /**
             * Callback when selected employee group is changed
             * Set and label the employees displayed in the employee dropdown
             */
            function setSelectedEmployeeGroup() {
                if ($scope.iSelEmpGroup < 0) {
                    return;
                }
                $scope.allEmps = supEmpGroupService.getEmpInfos($scope.iSelEmpGroup, !$scope.showSenators);
                setEmpLabels();

                $scope.selectedSup = $scope.supEmpGroups[$scope.iSelEmpGroup];

                // If the selected emp is already 0, setting it again will not trigger the watch
                // so we just call the function here if it is 0
                if ($scope.iSelEmp === 0) {
                    setSelectedEmployee();
                } else {
                    $scope.iSelEmp = 0;
                }
            }

            /**
             * Callback triggered when selected employee is changed
             * Sets the 'selectedEmp' variable
             */
            function setSelectedEmployee() {
                if ($scope.iSelEmp < 0) {
                    return;
                }
                $scope.selectedEmp = $scope.allEmps[$scope.iSelEmp];
            }

            function setSupGroupLabels() {
                var supName = appProps.user.firstName + " " + appProps.user.lastName;

                angular.forEach($scope.supEmpGroups, function (empGroup) {
                    if (empGroup.supId === appProps.user.employeeId) {
                        empGroup.dropDownLabel = supName;
                    } else {
                        empGroup.group = 'Supervisors Under ' + supName;
                        setDropDownLabel(empGroup);
                    }
                });
            }

            function setEmpLabels() {
                angular.forEach($scope.allEmps, function (emp) {
                    if (emp.empOverride) {
                        emp.group = 'Employee Overrides';
                    } else if (emp.supOverride) {
                        var supName = supEmpGroupService.getName(emp.supId);
                        emp.group = (supName && supName.lastName)
                            ? ($filter('possessive')(supName.lastName) + ' Employees')
                            : 'Sup Override Employees';
                    } else {
                        emp.group = 'Direct Employees';
                    }
                    setDropDownLabel(emp, emp.empOverride || emp.supOverride);
                });
            }

            /**
             * Sets the dropdown label for the given object
             * in the format {lastName} {first initial}. ({effective start date} - {effective end date})
             * @param emp
             * @param override
             */
            function setDropDownLabel(emp, override) {
                var startDate = override ? emp.effectiveStartDate : emp.supStartDate;
                var endDate = override ? emp.effectiveEndDate : emp.supEndDate;

                var supStartMoment = moment(startDate || '1970-01-01');
                var supEndMoment = moment(endDate || '2999-12-31');

                var name = emp.empLastName + ' ' + emp.empFirstName[0] + '.';

                // Start with just the start month
                var dates = supStartMoment.format('MMM YYYY');

                // If the range is ongoing, indicate it goes to 'Present'
                if (!supEndMoment.isBefore(moment(), 'day')) {
                    dates += ' - Present';
                }
                // Or if the range spans multiple months, include an end month
                else if (supStartMoment.isBefore(supEndMoment, 'month')) {
                    dates += ' - ' + supEndMoment.format('MMM YYYY');
                }
                emp.dropDownLabel = name + ' (' + dates + ')';
            }

            /**
             * Returns false if the employee is not currently supervised by the user
             * and the 'activeOnly' flag is set to true
             * Otherwise return true
             * @param empInfo
             * @returns {boolean}
             */
            function activeFilter (empInfo) {
                if (!$scope.activeOnly) {
                    return true;
                }
                return !moment().isAfter(empInfo.effectiveEndDate || empInfo.supEndDate, 'day');
            }

            /**
             * Returns true if the employee is NOT a senator
             * or if the 'showSenators' flag is set to true
             * @param empInfo
             * @returns {boolean}
             */
            function senatorFilter (empInfo) {
                return $scope.showSenators || !empInfo.senator;
            }

        }
    }
}
