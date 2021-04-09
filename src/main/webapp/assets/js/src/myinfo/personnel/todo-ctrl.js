(function () {

angular.module('essMyInfo')
    .controller('TodoCtrl', ['$scope', '$q', 'appProps', 'modals', 'TaskUtils',
                                   todoCtrl]);

function todoCtrl($scope, $q, appProps, modals, taskUtils) {

    var initialState = {
        assignments: {
            incomplete: [],
            complete: []
        },

        request: {
            assignments: false
        }
    };

    function init() {
        $scope.state = angular.copy(initialState);
        getAssignments();
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
    $scope.anyAssignments = function () {
        var assignments = $scope.state.assignments;
        return assignments.complete.length > 0 || assignments.incomplete.length > 0;
    };

    /* --- Request Methods --- */

    /**
     * Load all assignments for the user.  Categorize them as complete or incomplete.
     * @return {*}
     */
    function getAssignments() {
        var stateAssignments = $scope.state.assignments = {
            complete: [],
            incomplete: []
        };

        $scope.state.request.assignments = true;
        return taskUtils.getEmpAssignments(appProps.user.employeeId, true)
            .then(categorizeAssignments, $scope.handleErrorResponse)
            .finally(function () {
                $scope.state.request.assignments = false;
            });

        function categorizeAssignments(assignments) {
            angular.forEach(assignments, function(assignment) {
                console.log(assignment);
                if (assignment.completed) {
                    stateAssignments.complete.push(assignment);
                } else {
                    if (assignment.active) {
                        stateAssignments.incomplete.push(assignment);
                    }
                }
            });

            var activeUnacknowledgedTasks = [];
            while (stateAssignments.incomplete.length > 0) {
                var assignedTask = stateAssignments.incomplete.shift();
                if ( assignedTask.task.active === true ) {
                    activeUnacknowledgedTasks.push(assignedTask);
                }
            }
            stateAssignments.incomplete = activeUnacknowledgedTasks;
        }
    }
    init();
}

})();
