
angular.module('essTime')
    .controller('EmployeeSearchCtrl', ['$scope', 'modals', 'AllowanceUtils',
                                       'AccrualPeriodApi', 'AllowanceApi', employeeSearchCtrl])
;

function employeeSearchCtrl($scope, modals, allowanceUtils, accrualPeriodApi, allowanceApi) {

    $scope.$watch('selectedEmp', getAccruals);
    $scope.$watch('selectedEmp', getAllowance);

    function getAccruals () {
        $scope.accruals = null;
        if (!$scope.selectedEmp) {
            return;
        }

        if ($scope.selectedEmp.senator) {
            return;
        }

        if (['RA', 'SA'].indexOf($scope.selectedEmp.payType) < 0) {
            return;
        }

        var params = {
            empId: $scope.selectedEmp.empId,
            beforeDate: moment().format('YYYY-MM-DD')
        };

        $scope.loadingAccruals = true;
        accrualPeriodApi.get(params, onSuccess, onFail)
            .$promise.finally(function () {
                $scope.loadingAccruals = false;
            });

        function onSuccess (resp) {
            $scope.accruals = resp.result;
        }

        function onFail (resp) {
            modals.open('500', {details: resp});
            console.error(resp);
        }

    }

    function getAllowance () {
        $scope.allowance = null;

        if (!$scope.selectedEmp) {
            return;
        }

        if ($scope.selectedEmp.senator) {
            return;
        }

        if ($scope.selectedEmp.payType !== 'TE') {
            return;
        }

        var params = {
            empId: $scope.selectedEmp.empId,
            year: moment().year()
        };

        $scope.loadingAllowance = true;
        allowanceApi.get(params, onSuccess, onFail)
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

        function onFail (resp) {
            modals.open('500', {details: resp});
            console.error(resp);
        }

    }
}