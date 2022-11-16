(function () {
    angular.module('essMyInfo')
        .controller('EthicsCourseLiveCtrl', ['$scope', '$routeParams', '$location', 'appProps', 'modals','TaskUtils', 'PECEthicsCodeApi', EthicsCourseLiveCtrl]);

    function EthicsCourseLiveCtrl($scope, $routeParams, $location, appProps, modals, taskUtils, ethicsCodeApi) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initState = {
            empId: appProps.user.employeeId,
            taskId: $routeParams.taskId,
            assignment: null,
            loading: false,
            codes: [],
            incorrectCode: false,

            request: {
                assignment: false,
                code: false
            }
        };

        init();

        function init() {
            $scope.state = angular.copy(initState);
            getEthicsCourseLiveAssignment();
        }

        function getEthicsCourseLiveAssignment() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;
            taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
                .then(setAssignment)
                .finally(function () {
                    $scope.state.loading = false;
                })
        }

        function setAssignment(assignment) {
            console.log(assignment)
            if (assignment.task.taskType === 'ETHICS_LIVE_COURSE') {
                $scope.state.assignment = assignment;
                $scope.state.codes = assignment.task.codes;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }

        $scope.submitEthicsCodes = function() {
            // console.log("Submitting ethics codes...")

            var codes = $scope.state.codes
                .map(function (codeObj) {
                    return codeObj.value;
                });
            // console.log(codes)
            var body = {
                empId: $scope.state.empId,
                taskId: $scope.state.taskId,
                codes: codes
            };
            console.log(body)
            $scope.state.request.code = true;
            ethicsCodeApi.save({}, body, onSubmitSuccess, onSubmitFail)
                .$promise
                .finally(function () {
                    $scope.state.request.code = false;
                });
        }

        /**
         * Navigate to the to-do page.
         */
        function toTodo() {
            $location.url($scope.todoPageUrl)
        }

        /**
         * Handle code submit success by refreshing task data and prompting the user to return to to-do page.
         */
        function onSubmitSuccess() {
            init();
            $scope.updatePersonnelTaskBadge();
            modals.open('code-submit-success').then(toTodo);
        }

        /**
         * Handle code submit failure.
         *
         * If the error was an incorrect code, set a flag in the state.
         * Otherwise trigger the standard error handler.
         */
        function onSubmitFail(resp) {
            var errorCode = ((resp || {}).data || {}).errorCode;
            if (errorCode === 'INVALID_PEC_CODE') {
                console.warn('user submitted one or more incorrect codes:', $scope.state.codes);
                $scope.state.incorrectCode = true;
            } else {
                $scope.handleErrorResponse(resp);
            }
        }
    }
})();
