(function () {
    angular.module('essMyInfo')
        .controller('EmpAckDocReportCtrl', ['$scope', 'modals', 'EmpAckReportApi', 'AcknowledgmentApi',
                                            empAckDocReportCtrl])
    ;

    function empAckDocReportCtrl($scope, modals, EmpAckReportApi, AckApi) {

        var initialState = {
            loadingAcks: false,
            yearDocMap: {},
            years: [],
            selectedYear: null,
            selectedDoc: null,
            unacknowledged: [],
            acknowledged: []
        };

        resetState();

        /* --- Watches --- */

        $scope.$watch('selectedEmp', onSelectedEmpChange);
        $scope.$watch('state.selectedYear', onSelectedYearChange);

        /* --- Display Methods --- */

        /**
         * Initiates a personnel acknowledgment for the given document by opening a prompt dialogue.
         * If the dialogue is confirmed, an acknowledgment will be posted for the given employee/document.
         * @param ackStatus
         */
        $scope.initiatePersonnelAck = function (ackStatus) {
            $scope.state.selectedDoc = ackStatus.ackDoc;
            modals.open('personnel-acknowledge-prompt')
                .then(function () {
                    postPersonnelAck(ackStatus.ackDoc);
                });
        };

        /* --- Internal methods --- */

        /**
         * Reset the scope state to the initial state.
         */
        function resetState() {
            $scope.state = angular.copy(initialState);
        }

        /**
         * When a new employee is selected, reset page data and request new data from the api
         */
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
                // Create a list of years based on the returned documents.
                var years = Object.keys($scope.state.yearDocMap).map(parseInt);
                years.sort().reverse();
                $scope.state.years = years;
                // Select the latest year by default
                $scope.state.selectedYear = years[0];
            }
        }

        /**
         * When the user selects a new year,
         * load acknowledgments for that year and categorize them for display.
         */
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

        /**
         * Post an acknowledgment on behalf of an employee.
         * On success, reload ack data for the employee and display a success message.
         * @param ackDoc
         */
        function postPersonnelAck(ackDoc) {
            var params = {
                empId: $scope.selectedEmp.empId,
                ackDocId: ackDoc.id
            };

            AckApi.save(params, {}, onSuccess, $scope.handleErrorResponse);

            function onSuccess() {
                onSelectedEmpChange();
                showPersonnelAckSuccessModal(ackDoc);
            }
        }

        function showPersonnelAckSuccessModal(ackDoc) {
            $scope.state.selectedDoc = ackDoc;
            modals.open('personnel-acknowledge-success');
        }
    }
})();