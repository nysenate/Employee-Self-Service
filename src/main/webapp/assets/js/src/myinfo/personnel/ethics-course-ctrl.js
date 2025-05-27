(function () {
    angular.module('essMyInfo')
        .controller('EthicsCourseCtrl', ['$scope', '$routeParams', 'appProps', 'TaskUtils', EthicsCourseCtrl]);

    function EthicsCourseCtrl($scope, $routeParams, appProps, taskUtils) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
            taskId: $routeParams.taskId,
            assignment: null,
            loading: false
        };

        init();

        function init() {
            $scope.state = angular.copy(initState);
            getEthicsAssignment();
        }

        function getEthicsAssignment() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;
            taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
                .then(setAssignment)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setAssignment(assignment) {
            if (assignment.task.taskType === 'ETHICS_COURSE') {
                $scope.state.assignment = assignment;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }
    }
})();