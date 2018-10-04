
angular.module('ess')
    .controller('EmployeeSearchCtrl', ['$scope', 'AccrualActiveYearsApi', 'AccrualPeriodApi',
                                       'AllowanceActiveYearsApi', 'AllowanceUtils', 'AllowanceApi',
                                       employeeSearchCtrl])
;

function employeeSearchCtrl($scope, accrualActiveYearsApi, accrualPeriodApi,
                            allowanceActiveYearsApi, allowanceUtils, allowanceApi) {

    $scope.$watch('selectedEmp', onSelectedEmpChange);

    function onSelectedEmpChange() {
        getAccrualYears();
        getAccruals();
        getAllowanceYears();
        getAllowance();
    }

    function getAccrualYears() {
        $scope.showAccrualHistory = false;
        $scope.showAccruals = false;

        if (!$scope.selectedEmp) {
            return;
        }

        var params = {empId: $scope.selectedEmp.empId};

        accrualActiveYearsApi.get(params, onSuccess, $scope.handleErrorResponse);

        function onSuccess(response) {
            $scope.showAccrualHistory = response.years && response.years.length > 0;
            $scope.showAccruals = $scope.showAccrualHistory &&
                response.years.indexOf(moment().year()) >= 0 &&
                $scope.selectedEmp.payType !== 'TE' &&
                !$scope.selectedEmp.senator;
        }
    }

    function getAllowanceYears() {
        $scope.showAllowanceHistory = false;

        if (!$scope.selectedEmp) {
            return;
        }

        var params = {empId: $scope.selectedEmp.empId};

        allowanceActiveYearsApi.get(params, onSuccess, $scope.handleErrorResponse);

        function onSuccess(response) {
            $scope.showAllowanceHistory = response.years && response.years.length > 0;
        }
    }

    function getAccruals () {
        $scope.accruals = null;

        // Cancel if employee is not eligible for accruals
        if (!$scope.selectedEmp || $scope.selectedEmp.senator ||
            ['RA', 'SA'].indexOf($scope.selectedEmp.payType) < 0 ||
            !$scope.selectedEmp.active) {
            return;
        }

        var params = {
            empId: $scope.selectedEmp.empId,
            beforeDate: moment().format('YYYY-MM-DD')
        };

        $scope.loadingAccruals = true;
        accrualPeriodApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.loadingAccruals = false;
            });

        function onSuccess (resp) {
            $scope.accruals = resp.result;
        }

    }

    function getAllowance () {
        $scope.allowance = null;

        // Cancel if employee is not eligible for allowance
        if (!$scope.selectedEmp || $scope.selectedEmp.senator ||
            $scope.selectedEmp.payType !== 'TE' ||
            !$scope.selectedEmp.active) {
            return;
        }

        var params = {
            empId: $scope.selectedEmp.empId,
            year: moment().year()
        };

        $scope.loadingAllowance = true;
        allowanceApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally( function () {
                $scope.loadingAllowance = false;
            });


        function onSuccess (resp) {
            $scope.allowance = resp.result[0];
            allowanceUtils.computeRemaining($scope.allowance, {
                beginDate: moment(),
                endDate: moment('3000-01-01')
            });
        }
    }
}