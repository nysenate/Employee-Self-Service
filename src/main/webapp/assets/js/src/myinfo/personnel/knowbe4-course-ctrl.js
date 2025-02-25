(function () {
    angular.module('essMyInfo')
        .controller('Knowbe4CourseCtrl', ['$scope', '$routeParams', '$location', 'appProps', 'modals','TaskUtils', Knowbe4CourseCtrl]);

    function Knowbe4CourseCtrl($scope, $routeParams, $location, appProps, modals, taskUtils) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
            empId: appProps.user.employeeId,
            taskId: $routeParams.taskId,
            assignment: null,
            loading: false,
            trainingDate: null,

            request: {
                assignment: false,
                code: false
            }
        };

        init();

        function init() {
            console.log($scope.state = angular.copy(initState));
            $scope.state = angular.copy(initState);
            getKnowBe4CourseAssignment();
        }

        function getKnowBe4CourseAssignment() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;
            console.log(taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId));
            taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
                .then(setAssignment)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setAssignment(assignment) {
            console.log(assignment)
            if (assignment.task.taskType === 'KNOWBE4_COURSE') {
                $scope.state.assignment = assignment;

                $scope.state.trainingDate = assignment.task.trainingDate;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }

        /**
         * Navigate to the to-do page.
         */
        function toTodo() {
            $location.url($scope.todoPageUrl)
        }
    }
})();
