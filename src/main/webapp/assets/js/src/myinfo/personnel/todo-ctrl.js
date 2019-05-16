(function () {

angular.module('essMyInfo')
    .controller('TodoCtrl', ['$scope', '$q', 'appProps', 'modals', 'TaskUtils',
                                   todoCtrl]);

function todoCtrl($scope, $q, appProps, modals, taskUtils) {

    var initialState = {
        tasks: {
            incomplete: [],
            complete: []
        },

        request: {
            tasks: false
        }
    };

    function init() {
        $scope.state = angular.copy(initialState);
        getTasks();
    }

    /* --- Display methods --- */

    /**
     * Return true if any requests are currently in progress.
     * @return {boolean}
     */
    $scope.isLoading = function () {
        var loading = false;
        angular.forEach($scope.state.request, function (status) {
            loading = loading || status;
        });
        return loading;
    };

    /**
     * Return true if there are any tasks, complete or otherwise
     */
    $scope.anyTasks = function () {
        var tasks = $scope.state.tasks;
        return tasks.complete.length > 0 || tasks.incomplete.length > 0;
    };

    /* --- Request Methods --- */

    /**
     * Load all tasks for the user.  Categorize them as complete or incomplete.
     * @return {*}
     */
    function getTasks() {
        var stateTasks = $scope.state.tasks = {
            complete: [],
            incomplete: []
        };

        $scope.state.request.tasks = true;
        return taskUtils.getEmpTasks(appProps.user.employeeId, true)
            .then(categorizeTasks, $scope.handleErrorResponse)
            .finally(function () {
                $scope.state.request.tasks = false;
            });

        function categorizeTasks(tasks) {
            angular.forEach(tasks, function(task) {
                if (task.completed) {
                    stateTasks.complete.push(task);
                } else {
                    stateTasks.incomplete.push(task);
                }
            })
        }
    }
    init();
}

})();
