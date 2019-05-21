(function () {
    angular.module('essMyInfo')
        .controller('LegEthicsCtrl', ['$scope', 'appProps', 'TaskUtils', legEthicsCtrl]);

    var legEthicsTaskType = 'MOODLE_COURSE';
    var legEthicsTaskNum = 1;

    function legEthicsCtrl($scope, appProps, taskUtils) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
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
            var empId = appProps.user.employeeId,
                taskType = legEthicsTaskType,
                taskNum = legEthicsTaskNum;
            taskUtils.getPersonnelAssignedTask(empId, taskType, taskNum)
                .then(setTask)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setTask(task) {
            $scope.state.task = task;
        }
    }
})();