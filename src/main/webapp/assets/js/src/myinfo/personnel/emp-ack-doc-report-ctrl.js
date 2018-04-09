(function () {
    angular.module('essMyInfo')
        .controller('EmpAckDocReportCtrl', ['$scope', 'EmpAckReportApi' ,
                                            empAckDocReportCtrl])
    ;

    function empAckDocReportCtrl($scope, EmpAckReportApi) {

        var initialState = {
            loadingAcks: false,
            yearDocMap: {},
            years: [],
            selectedYear: null,
            unacknowledged: [],
            acknowledged: []
        };

        resetState();

        $scope.$watch('selectedEmp', onSelectedEmpChange);
        $scope.$watch('state.selectedYear', onSelectedYearChange);

        $scope.getYears = function () {
            return Object.keys($scope.state.yearDocMap);
        };

        function resetState() {
            $scope.state = angular.copy(initialState);
        }

        function onSelectedEmpChange() {
            resetState();

            if (!$scope.selectedEmp) {
                return;
            }

            var params = {
                empId: $scope.selectedEmp.empId
            };
            $scope.state.loadingAcks = true;
            return EmpAckReportApi.get(params, onSuccess, $scope.handleErrorResponse)
                .$promise.finally(function () {
                    $scope.state.loadingAcks = false;
                });

            function onSuccess(resp) {
                resp.acks.forEach(function (ack) {
                    var year = moment(ack.ackDoc.effectiveDateTime).year();
                    if (!$scope.state.yearDocMap[year]) {
                        $scope.state.yearDocMap[year] = [];
                    }
                    var docs = $scope.state.yearDocMap[year];
                    docs.push(ack);
                });
                var years = Object.keys($scope.state.yearDocMap).map(parseInt);
                years.sort().reverse();
                $scope.state.years = years;
                $scope.state.selectedYear = years[0];
            }
        }

        function onSelectedYearChange() {
            if (!$scope.state.selectedYear) {
                return;
            }
            console.log('selected year', $scope.state.selectedYear);
            var ackStatuses = $scope.state.yearDocMap[$scope.state.selectedYear] || [];
            var unacked = $scope.state.unacknowledged = [];
            var acked = $scope.state.acknowledged = [];

            ackStatuses.forEach(function (status) {
                if (status.ack === null) {
                    unacked.push(status);
                } else {
                    acked.push(status);
                }
            });
        }
    }
})();