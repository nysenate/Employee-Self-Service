(function () {
    var essTime = angular.module('essTime');

    essTime.factory('UpdateRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1//accruals/request");
    }]);

    essTime.directive('timeOffRequestView', ['appProps', 'UpdateRequestsApi', requestViewDirective]);


    /*
            This directive makes the api calls to update a request since the places it is
            used all make the same api calls. Including those calls in the directive cuts
            down on duplicate code.
     */
    function requestViewDirective(appProps, updateRequestsApi) {
        return {
            scope: {
                data: '=',
                mode: '='
            },
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-view',
            link: function ($scope) {

                /**
                 * Converts all the dates in a request into strings in the format
                 * 'Day Mon DD, YYYY' so they display properly when viewing a request
                 */
                $scope.dateToString = function() {
                    $scope.data.days.forEach(function(day) {
                        day.date = new Date(day.date);
                        day.date = day.date.toDateString();
                    });
                };

                /**
                 * Converts all strings back into dates so they can be translated into
                 * the date picker
                 */
                $scope.stringToDate = function() {
                    $scope.data.days.forEach(function (day) {
                        day.date = new Date(day.date);
                    });
                };

                /**
                 * Function to be executed when the page loads.
                 * This function makes sure that the dates are
                 * formatted properly either for display or for
                 * input (date picker)
                 */
                $scope.onloadFn = function() {
                    if($scope.mode==="input") {
                        $scope.stringToDate();
                    } else {
                        $scope.dateToString();
                    }
                };

                $scope.empId = appProps.user.employeeId;
                $scope.userType = "";
                $scope.userType = $scope.empId === $scope.data.employeeId ? "E" : "S";
                $scope.otherContact = $scope.userType === "E" ? "Supervisor" : "Employee";
                $scope.addedComment = "";
                $scope.accruals = {
                    vacation: 0,
                    personal: 0,
                    sick: 0
                };
                $scope.accrualsPost = {
                    vacation: 0,
                    personal: 0,
                    sick: 0
                };

                $scope.miscTypeList = appProps.miscLeaves;

                /**
                 * Function that executes when one of the values for the hours changes.
                 * It updates the totalHours box, which is not able to be edited by the
                 * user directly.
                 */
                $scope.updateTotals = function() {
                    $scope.data.days.forEach(function(day) {
                        day.totalHours = day.workHours + day.vacationHours + day.personalHours + day.sickEmpHours
                            + day.sickFamHours + day.miscHours;
                        console.log(day.total);
                    });
                };

                /**
                 * Function to delete the selected days from a request
                 */
                $scope.deleteSelected = function() {
                    $scope.data.days = $scope.data.days.filter($scope.isNotChecked);
                };

                /**
                 * Return true for a given day if that day is not selected.
                 * Reason for returning true for a negative condition is that the
                 * filter function in $scope.deleteSelected (where this function is
                 * used) needs a positive condition which determines which requests to
                 * keep.
                 * @param day
                 * @returns {boolean}
                 */
                $scope.isNotChecked = function(day) {
                    return !day.checked;
                };

                /**
                 * Function to add a blank day to a request (the blank day will show up as a blank
                 * row in the table)
                 */
                $scope.addDay = function() {
                   $scope.data.days.push({date:null, checked: false, workHours: 0, holidayHours: 0,
                                       vacationHours: 0, personalHours: 0, sickEmpHours:0,
                                       sickFamHours:0, miscHours:0, miscType: null, totalHours: 0});
                };


                /**
                 * Function that puts the directive in edit mode when the edit button is pressed.
                 */
                $scope.editMode = function() {
                    $scope.stringToDate();
                    $scope.mode = "input";
                };

                /**
                 * Function to obtain an object to send in an API call as the request body.
                 * The object created will be used for a call to update a time-off request.
                 * @param statusType (either SAVED or SUBMITTED)
                 * @returns {{comments: *, endDate: *, days, employeeId: *, supervisorId: *, startDate: *, status: *}}
                 */
                $scope.getSendObject = function(statusType) {
                    $scope.stringToDate();
                    $scope.data.days = $scope.data.days.sort(function(a,b){
                        return a.date - b.date;
                    });
                    if($scope.addedComment !== "") {
                        $scope.data.comments.push({
                                                      text: $scope.addedComment,
                                                      authorId: $scope.data.employeeId
                                                  });
                    }
                    return {
                        status: statusType,
                        employeeId: $scope.empId,
                        supervisorId: $scope.data.supervisorId,
                        startDate: $scope.data.days[0].date,
                        endDate: $scope.data.days[$scope.data.days.length-1].date,
                        days: $scope.data.days,
                        comments: $scope.data.comments
                    };
                };


                /*
                    Below are the API calls necessary for this directive. They currently do
                    not work due to a permissions issue, so they are commented out.
                 */

                $scope.saveRequest = function() {
                    var sendObject = $scope.getSendObject("SAVED");

                    //api call to save the request
                    // updateRequestsApi.save(sendObject).$promise.then(
                    //     //on success
                    //     function(data) {
                    //         console.log("Success!");
                    $scope.dateToString();
                    $scope.mode = "output";
                    //     },
                    //     //on failure
                    //     function(data) {
                    //         console.log("There was an error while attempting to save your request.");
                    //     }
                    // );
                    //blue check "Saved!" at the top that fades

                };

                $scope.submitRequest = function() {
                    var sendObject = $scope.getSendObject("SUBMITTED");

                    //api call to submit the request
                    // updateRequestsApi.save(sendObject).$promise.then(
                    //     //on success
                    //     function(data) {
                    //         console.log("Success!");
                    $scope.dateToString();
                    $scope.mode = "output";
                    //     },
                    //     //on failure
                    //     function(data) {
                    //         console.log("There was an error while attempting to submit your request.");
                    //     }
                    // );
                    //green check "Submitted to supervisor!" at the top that fades

                };


            }
        }
    }
})();