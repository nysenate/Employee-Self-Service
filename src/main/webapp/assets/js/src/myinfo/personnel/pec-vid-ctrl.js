(function () {
    angular.module('essMyInfo')
        .controller('PecVidCtrl', ['$scope', '$routeParams', '$location', '$sce',
                                   'appProps', 'modals', 'TaskUtils', 'PECVidCodeApi',
                                   pecVidCtrl]);

    /**
     * Controller for the page that displays PEC Videos and contains a form to submit video codes.
     */
    function pecVidCtrl($scope, $routeParams, $location, $sce, appProps, modals, taskUtils, vidCodeApi) {

        $scope.todoPageUrl = appProps.ctxPath + '/myinfo/personnel/todo';

        var initialState = {
            empId: appProps.user.employeeId,
            videoId: null,
            videoUrl: null,
            task: null,
            taskFound: false,
            acknowledged: false,
            codes: [],
            incorrectCode: false,

            request: {
                task: false,
                code: false
            }
        };

        init();

        /* --- Display methods --- */

        $scope.submitCodes = submitCodes;

        /* --- Internal Methods --- */

        /**
         * Initialize the state and begin loading the video task information
         */
        function init() {
            $scope.state = angular.copy(initialState);
            $scope.state.taskId = parseInt($routeParams.videoId);
            fetchVideoTask();
        }

        /**
         * Load updated information for the selected video task.
         */
        function fetchVideoTask() {
            clearTask();
            $scope.state.request.task = true;
            taskUtils.getPersonnelTaskAssignment($scope.state.empId, $scope.state.taskId)
                .then(setVideoTask)
                .finally(function () {
                    $scope.state.request.task = false;
                })
        }

        /**
         * Reset information for the current task.
         */
        function clearTask() {
            $scope.state.task = null;
            $scope.state.taskFound = false;
            $scope.state.videoUrl = null;
            $scope.state.codes = [];
        }

        /**
         * Set information for the current task using the given task object.
         */
        function setVideoTask(task) {
            $scope.state.taskFound = true;
            $scope.state.task = task;
            $scope.state.videoUrl = $sce.trustAsResourceUrl(appProps.ctxPath + $scope.state.task.taskDetails.path);
            $scope.state.codes = task.taskDetails.codes;
        }

        /**
         * Submits the codes entered in the form.
         */
        function submitCodes() {
            var codes = $scope.state.codes
                .map(function (codeObj) {
                    return codeObj.value;
                });
            var body = {
                empId: $scope.state.empId,
                taskId: $scope.state.taskId,
                codes: codes
            };
            $scope.state.request.code = true;
            vidCodeApi.save({}, body, onSubmitSuccess, onSubmitFail)
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
            modals.open('code-submit-success')
                .then(toTodo);
        }

        /**
         * Handle code submit failure.
         *
         * If the error was an incorrect code, set a flag in the state.
         * Otherwise trigger the standard error handler.
         */
        function onSubmitFail(resp) {
            var errorCode = ((resp || {}).data || {}).errorCode;
            if (errorCode === 'INVALID_PEC_VIDEO_CODE') {
                console.warn('user submitted one or more incorrect codes:', $scope.state.codes);
                $scope.state.incorrectCode = true;
            } else {
                $scope.handleErrorResponse(resp);
            }
        }

    }
})();