(function () {
    angular.module('essMyInfo')
        .controller('LegEthicsCtrl', ['$scope', '$routeParams', 'appProps', 'TaskUtils', legEthicsCtrl]);

    function legEthicsCtrl($scope, $routeParams, appProps, taskUtils) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
            taskId: $routeParams.taskId,
            task: null,
            loading: false
        };

        init();

        function init() {
            $scope.state = angular.copy(initState);
            getLegEthicsTask();
        }

        function getLegEthicsTask() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;
            taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
                .then(setTask)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setTask(task) {
            if (task.taskDetails.taskType === 'MOODLE_COURSE') {
                $scope.state.task = task;
            }
        }
    }
})();