(function () {
    angular.module('essMyInfo')
        .controller('EthicsCourseLiveCtrl', ['$scope', '$routeParams', 'appProps', 'TaskUtils', EthicsCourseLiveCtrl]);

    // Dummy assignment for testing purposes. Needs to be replaced with a call to taskUtils.getPersonnelTaskAssignment()
    var stubAssignment = {
        "empId": 12756,
        "taskId": 11,
        "timestamp": null,
        "updateUserId": null,
        "completed": false,
        "active": true,
        "task": {
            "taskId": 11,
            "taskType": "ETHICS_COURSE_LIVE",
            "title": "2021 Ethics Review Live",
            "effectiveDateTime": "2021-04-30T00:00:00",
            "endDateTime": null,
            "active": true,
            "url": "https://my.nysenate.gov/department/personnel/training",
            getCourseUrl: function() {return "https://my.nysenate.gov/department/personnel/training";},
            "codes": [
                {
                    "videoId": 4,
                    "sequenceNo": 1,
                    "label": "First Code"
                },
                {
                    "videoId": 4,
                    "sequenceNo": 2,
                    "label": "Second Code"
                }
            ]
        }
    }

    function EthicsCourseLiveCtrl($scope, $routeParams, appProps, taskUtils) {

        var initState = {
            taskId: $routeParams.taskId,
            assignment: null,
            loading: false,
            codes: []
        };

        init();

        function init() {
            $scope.state = angular.copy(initState);
            getEthicsCourseLiveAssignment();
        }

        console.log(stubAssignment)

        function getEthicsCourseLiveAssignment() {
            $scope.state.loading = true;
            var empId = appProps.user.employeeId;

            // TODO replace these 2 lines with the below (commented out) call to taskUtils.getPersonnelTaskAssignment.
            $scope.state.assignment = stubAssignment;
            $scope.state.loading = false;
            // taskUtils.getPersonnelTaskAssignment(empId, $scope.state.taskId)
            //     .then(setAssignment)
            //     .finally(function () {
            //         $scope.state.loading = false;
            //     })
        }

        function setAssignment(assignment) {
            if (assignment.task.taskType === 'ETHICS_COURSE_LIVE') {
                $scope.state.assignment = assignment;
            } else {
                $scope.handleErrorResponse(assignment);
            }
        }

        $scope.submitCodes = function() {
            console.log("Submitting codes...")
        }
    }
})();
