(function () {
    angular.module('essMyInfo')
        .controller('EmpAckDocReportCtrl', ['$scope', '$q', 'appProps', 'modals','EmpAckReportApi' ,
                                            empAckDocReportCtrl])
    ;

    function empAckDocReportCtrl($scope, $q, appProps, modals,EmpAckReportApi) {

        $scope.ackStatusesReady = false;
        $scope.displayAckStatuses = [];

        $scope.$watch('selectedEmp', onSelectedEmpChange);

        function onSelectedEmpChange() {
            $scope.displayAckStatuses = [];
            $scope.ackStatusesReady = false;


            if (!$scope.selectedEmp) {
                return;
            }

            var params = {
                empId: $scope.selectedEmp.empId
            };
            var requestAcquireAcks = true;
            return EmpAckReportApi.get(params, onSuccess, $scope.handleErrorResponse)
                .$promise.finally(function () {
                    requestAcquireAcks = false;
                });

            function onSuccess(resp) {
                angular.forEach(resp.acks, function (ackStatus) {

                    var status = ackStatus.ackDoc.title + "-" +
                        new Date(ackStatus.ackDoc.effectiveDateTime).getFullYear() + " ";
                    if (ackStatus.ack == null) {
                        status = status + " WAS NOT ACKNOWLEDGED";
                    }
                    else {
                        status = status + " Acked on: " + ackStatus.ack.timestamp;
                    }
                    $scope.displayAckStatuses.push(status);
                });
                $scope.ackStatusesReady = true;
                //console.log($scope.displayAckStatuses);
            }

        }

    }

})();