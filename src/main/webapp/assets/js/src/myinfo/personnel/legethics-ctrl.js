(function () {
    angular.module('essMyInfo')
        .controller('LegEthicsCtrl', ['$scope', '$routeParams', 'appProps', 'TaskUtils', legEthicsCtrl]);

    function legEthicsCtrl($scope, $routeParams, appProps, taskUtils) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
            taskId: $routeParams.taskId,
            assignment: null,
            loading: false
        };

        init();

        function init() {
            $scope.state = angular.copy(initState);
            getLegEthicsAssignment();
        }

        function getLegEthicsAssignment() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;
            taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
                .then(setAssignment)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setAssignment(assignment) {
            if (assignment.task.taskType === 'MOODLE_COURSE') {
                $scope.state.assignment = assignment;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }
    }
})();