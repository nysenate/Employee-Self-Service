(function () {

    angular.module('essMyInfo')
        .controller('MyInfoMainCtrl', ['$scope', '$q', 'appProps', 'badgeService', 'TaskUtils', myInfoCtrl])
    ;

    /**
     * The wrapping controller that is the parent of the nav menu and view content.
     */
    function myInfoCtrl($scope, $q, appProps, badgeService, taskUtils) {

        $scope.updatePersonnelTaskBadge = function () {

            return taskUtils.getEmpAssignments(appProps.user.employeeId, true)
                .then(setCount)
                .catch($scope.handleErrorResponse)
            ;

            function setCount(assignments) {
                var count = assignments
                    .filter(function (task) {
                        return task.hasOwnProperty('completed') && !task.completed
                    })
                    .length;
                badgeService.setBadgeValue('incompleteTasks', count);
            }
        };

        $scope.updatePersonnelTaskBadge();
    }

})();
