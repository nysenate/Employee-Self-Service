var essMyInfo = angular.module('essMyInfo');

essMyInfo.controller('EmpTransactionHistoryCtrl',
    ['$scope', '$http', 'appProps', '$q', 'EmpTransactionTimelineApi', 'modals',
    function($scope, $http, appProps, $q, EmpTransactionTimelineApi, modals) {

    $scope.state = {
        empId: appProps.user.employeeId,
        timeline: {}
    };

    $scope.getTimeline = function() {
        var deferred = $q.defer();
        EmpTransactionTimelineApi.get({empId: $scope.state.empId},
            function(resp) {
                if (resp.success && resp.total > 0) {
                    var seenTx = {};  // Used for filtering out multiple tx codes that have the same effect date.
                    angular.forEach(resp.transactions.reverse(), function(tx) {
                        if (!$scope.state.timeline[tx.effectDate]) $scope.state.timeline[tx.effectDate] = [];
                        if (!seenTx[tx.effectDate]) seenTx[tx.effectDate] = {};
                        if (!seenTx[tx.effectDate][tx.transCode]) {
                            $scope.state.timeline[tx.effectDate].push(tx);
                            seenTx[tx.effectDate][tx.transCode] = true;
                        }
                    });
                    seenTx = null;
                }
                else {
                    $scope.state.timeline = false;
                }
                deferred.resolve();
            },
            function(resp) {
                $scope.state.timeline = false;
                $scope.handleErrorResponse(resp);
                deferred.reject("Failed to retrieve timeline.");
            });
        return deferred;
    };

    $scope.init = function() {
        $scope.getTimeline();
    }();
}]);