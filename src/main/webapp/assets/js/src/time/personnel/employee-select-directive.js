angular.module('essTime')
    .directive('employeeSelect', ['appProps', 'supEmpGroupService', employeeSelectDirective]);

function employeeSelectDirective(appProps, supEmpGroupService) {
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

            $scope.selectSubject = $attrs.selectSubject;

            /**
             * Wait for supervisor emp groups to load
             * then set local variables and label emp groups
             */
            supEmpGroupService.init().then(function () {
                $scope.iSelEmpGroup = 0;
                $scope.supEmpGroups = supEmpGroupService.getSupEmpGroupList();
                setSupGroupLabels();
            });

            $scope.$watch('iSelEmpGroup', setSelectedEmployeeGroup);

            $scope.$watch('iSelEmp', setSelectedEmployee);

            /**
             * Callback when selected employee group is changed
             * Set and label the employees displayed in the employee dropdown
             */
            function setSelectedEmployeeGroup() {
                if ($scope.iSelEmpGroup < 0) {
                    return;
                }
                $scope.allEmps = supEmpGroupService.getEmpInfos($scope.iSelEmpGroup);
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
                        empGroup.group = 'Employees Under ' + supName;
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
                            ? (supName.lastName + '\'s Employees')
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
        }
    }
}
