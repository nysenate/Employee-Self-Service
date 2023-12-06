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
            tempCodes:[],
            incorrectCode: false,
            trainingDate: null,

            request: {
                assignment: false,
                code: false
            }
        };


        //Placeholder Codes added now that the ethics_code table will need to accommodate multiple entries
        var tempCodesEntries = [
            {
                "videoId": 2,
                "sequenceNo": 1,
                "label": "First Code"
            },
            {
                "videoId": 2,
                "sequenceNo": 2,
                "label": "Second Code"
            }
        ]
        console.log(tempCodesEntries);

        init();

        function init() {
            console.log($scope.state = angular.copy(initState));
            $scope.state = angular.copy(initState);
            getEthicsCourseLiveAssignment();
        }

        function getEthicsCourseLiveAssignment() {
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
            if (assignment.task.taskType === 'ETHICS_LIVE_COURSE') {
                $scope.state.assignment = assignment;
                $scope.state.codes = tempCodesEntries;
                console.log(assignment.task.codes);
                //assignment.task.codes=tempCodesEntries;
                console.log(assignment.task.codes);
                //console.log(this.tempCodes);
                //console.log(this.codes);
                $scope.state.trainingDate = assignment.task.trainingDate;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }

        $scope.submitEthicsCodes = function() {
            // console.log("Submitting ethics codes...")

            var trainingDate =  $scope.state.trainingDate
            console.log($scope.state);
            var codes = $scope.state.codes
                .map(function (codeObj) {
                    return codeObj.value;
                });
            // console.log(codes)
            var body = {
                empId: $scope.state.empId,
                taskId: $scope.state.taskId,
                codes: codes,
                trainingDate: trainingDate
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
