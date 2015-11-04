var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpSummaryCtrl',
    ['$scope', '$http', 'appProps', 'EmpInfoApi', 'EmpActiveYearsApi', 'EmpTransactionsApi', 'EmpTransactionSnapshotApi', 'modals',
        function($scope, $http, appProps, EmpInfoApi, EmpActiveYearsApi, EmpTransactionsApi, EmpTransactionSnapshotApi, modals) {

            $scope.state = {
                empId: appProps.user.employeeId,
                emp: null
            };

            $scope.fetchEmployeeData = function() {
                EmpInfoApi.get({empId: $scope.state.empId, detail: true}, function(resp) {
                    if (resp.success) {
                        $scope.state.emp = resp.employee;
                    }
                    EmpTransactionSnapshotApi.get({empId: $scope.state.empId}, function(resp) {
                        if (resp.success) {
                            $scope.state.emp.snapshot = resp.snapshot.items;
                        }
                    });
                });
            };

            $scope.init = function() {
                $scope.fetchEmployeeData();
            };

            $scope.init();
        }
    ]
);