(function () {
    var essTime = angular.module('essTime');

    essTime.factory('UpdateRequestsApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request", {
            request: '@request'
        });
    }]);

    essTime.factory('RequestApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals/request/:requestId");
    }]);

    essTime.factory('EmployeeInfoApi', ['$resource', function ($resource) {
        return $resource("/api/v1/employees");
    }]);

    essTime.factory('AccrualApi', ['$resource', function ($resource) {
        return $resource("/api/v1/accruals");
    }]);

    essTime.factory('HolidayApi', ['$resource', function ($resource) {
        return $resource("/api/v1/holidays");
    }]);

    essTime.factory('PayPeriodApi', ['$resource', function ($resource) {
        return $resource("/api/v1/periods/:periodTypeStr");
    }]);

    essTime.directive('timeOffRequestView', ['appProps', 'UpdateRequestsApi', 'RequestApi', 'EmployeeInfoApi',
                                             'AccrualApi', 'HolidayApi', 'PayPeriodApi',
                                             'TimeOffRequestValidationService', requestViewDirective]);


    /*
            This directive makes the api calls to update a request since the places it is
            used all make the same api calls. Including those calls in the directive cuts
            down on duplicate code.
     */
    function requestViewDirective(appProps, updateRequestsApi, RequestApi, EmployeeInfoApi,
                                  AccrualApi, HolidayApi, PayPeriodApi, TimeOffRequestValidationService) {
        return {
            scope: {
                data: '=',
                mode: '='
            },
            templateUrl: appProps.ctxPath + '/template/time/accrual/time-off-request-view',
            link: function ($scope) {
                $scope.pageLoaded = true;

                /**
                 * Function to be executed when the page loads.
                 */
                $scope.onloadFn = function () {
                    $scope.pageLoaded = true;
                };

                $scope.empId = appProps.user.employeeId;
                var empInfoArgs = {
                    'empId': $scope.empId,
                    'detail': true
                };
                EmployeeInfoApi.get(empInfoArgs).$promise.then(
                    function (data) {
                        $scope.supId = data.employee.supervisorId;
                    }, function (data) {
                        console.log("There was an error accessing employee data.", data);
                    }
                );

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
                var accrualArgs = {
                    'empId': $scope.empId,
                    'beforeDate': new Date().toISOString().substr(0, 10)
                };
                AccrualApi.get(accrualArgs).$promise.then(
                    function (data) {
                        $scope.accruals.personal = data.result.personalAvailable;
                        $scope.accruals.vacation = data.result.vacationAvailable;
                        $scope.accruals.sick = data.result.sickAvailable;
                        $scope.accrualsPost.personal = data.result.personalAvailable;
                        $scope.accrualsPost.vacation = data.result.vacationAvailable;
                        $scope.accrualsPost.sick = data.result.sickAvailable;
                    },
                    function (data) {
                        console.log("There was an error accessing accrual data.", data);
                    }
                );

                $scope.userType = "";
                $scope.userType = $scope.empId === $scope.data.employeeId ? "E" : "S";
                $scope.otherContact = $scope.userType === "E" ? "Supervisor" : "Employee";
                //$scope.addedComment = "";
                $scope.miscTypeList = appProps.miscLeaves;
                $scope.validRequest = true;
                $scope.validationErrorMessages = [];

                /**
                 * Function to check holiday hours when a date is changed in the date picker.
                 * If the new date is a holiday, the holiday hours column will be updated.
                 * Also updates the pay period of the day object.
                 *
                 * @param day - the day object that we are determining holiday hours for.
                 */
                $scope.datePickerChanged = function (day) {
                    day.dateStr = day.date.toDateString();
                    var isoDate = day.date.toISOString().substr(0, 10);
                    var params = {
                        'fromDate': isoDate,
                        'toDate': isoDate
                    };
                    HolidayApi.get(params).$promise.then(
                        function (data) {
                            var holidays = data.holidays;
                            if (holidays.length > 0) { //the day was a holiday
                                day.holidayHours = holidays[0].hours;
                                day.totalHours = holidays[0].hours;
                            } else { // the day was NOT a holiday
                                day.holidayHours = null;
                            }
                        },
                        function (data) {
                            console.log("There was an error retrieving holiday data: ", data);
                        }).finally(function () {
                            $scope.data.days = $scope.data.days.sort(function (a, b) {
                            return a.date - b.date;
                        });
                        params.periodTypeStr = "AF";
                        PayPeriodApi.get(params).$promise.then(
                            function (data) {
                                day.payPeriod = data.periods[0].payPeriodNum;
                            },
                            function (data) {
                                console.log("There was an error retrieving pay period data: ", data);
                            }
                        ).finally( function () {
                            $scope.getFirstDatesInPayPeriods(); //must be re-done, as the dates have changed.

                        })
                    });
                };

                /**
                 * Function that executes when one of the values for the hours changes.
                 * It updates the totalHours box, which is not able to be edited by the
                 * user directly.
                 * It will also update the leftover accrual values
                 */
                $scope.updateTotals = function () {
                    var vacationUsed = 0, personalUsed = 0, sickUsed = 0;
                    $scope.data.days.forEach(function (day) {
                        day.totalHours = day.workHours + day.vacationHours + day.personalHours + day.sickEmpHours
                            + day.sickFamHours + day.miscHours + day.holidayHours;
                        vacationUsed += day.vacationHours;
                        personalUsed += day.personalHours;
                        sickUsed += day.sickEmpHours + day.sickFamHours;
                    });
                    $scope.accrualsPost.vacation = $scope.accruals.vacation - vacationUsed;
                    $scope.accrualsPost.personal = $scope.accruals.personal - personalUsed;
                    $scope.accrualsPost.sick = $scope.accruals.sick - sickUsed;
                };

                /**
                 * Function to delete the selected days from a request
                 */
                $scope.deleteSelected = function () {
                    $scope.pageLoaded = false;
                    $scope.data.days = $scope.data.days.filter($scope.isNotChecked);
                    $scope.onloadFn();
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
                $scope.isNotChecked = function (day) {
                    return !day.checked;
                };

                /**
                 * Function to add a blank day to a request. The new day will be "today"
                 * if it is the first in the request. Otherwise, it will be the day after
                 * the last day in the request. The function will also check if the date
                 * is a holiday and then will get the pay period for the date.
                 */
                $scope.addDay = function () {
                    var newDate = new Date();
                    var dayObj = {
                        date: null, checked: false, workHours: null, holidayHours: null,
                        vacationHours: null, personalHours: null, sickEmpHours: null,
                        sickFamHours: null, miscHours: null, miscType: null, totalHours: null
                    };

                    /*First day added is today's date*/
                    if ($scope.data.days.length === 0) {
                        dayObj.date = newDate;
                    } else { /*Add the next calendar day after the last day entry*/
                        var prevDate = $scope.data.days[$scope.data.days.length - 1].date;
                        if (prevDate === null) {
                            dayObj.date = new Date();
                        } else {
                            newDate.setTime(prevDate.getTime() + (24 * 60 * 60 * 1000));
                            dayObj.date = newDate;
                        }
                    }

                    dayObj.dateStr = dayObj.date.toDateString();

                    /*Check if the day is a holiday. If so, include the holiday hours.*/
                    var params = {
                        'fromDate': newDate.toISOString().substr(0, 10),
                        'toDate': newDate.toISOString().substr(0, 10)
                    };
                    HolidayApi.get(params).$promise.then(
                        function (data) {
                            var holidays = data.holidays;
                            if (holidays.length > 0) {
                                dayObj.holidayHours = holidays[0].hours;
                                dayObj.totalHours = holidays[0].hours;
                            }
                        },
                        function (data) {
                            console.log("There was an error retrieving holiday data: ", data);
                        }
                    ).finally(function () {
                        /* Get the pay period number for the day*/
                        params.periodTypeStr = "AF";
                        PayPeriodApi.get(params).$promise.then(
                            function (data) {
                                dayObj.payPeriod = data.periods[0].payPeriodNum;
                            },
                            function (data) {
                                console.log("There was an error retrieving pay period data: ", data);
                            }
                        ).finally(function () {
                            $scope.data.days.push(dayObj);
                            $scope.getFirstDatesInPayPeriods();
                            $scope.updateTotals();
                        });
                    });
                };


                /**
                 * Function that puts the directive in edit mode when the edit button is pressed.
                 */
                $scope.editMode = function () {
                    $scope.pageLoaded = false;
                    $scope.mode = "input";
                    $scope.onloadFn();
                };

                /**
                 * Function to perform validation on a request before it is saved or submitted.
                 *
                 * @returns true - the request is valid and can be submitted/saved
                 *          false - the request does not pass validation
                 */
                $scope.validate = function () {
                    $scope.data.empId = $scope.empId; //needed for employee validation
                    $scope.data.supId = $scope.supId; //needed for supervisor validation
                    $scope.validationErrorMessages = TimeOffRequestValidationService.runChecks($scope.data, $scope.accruals);
                    $scope.validRequest = ($scope.validationErrorMessages.length === 0);
                    return $scope.validRequest;
                };

                /**
                 * Function to obtain an object to send in an API call as the request body.
                 * The object created will be used for a call to update a time-off request.
                 * @param statusType (either SAVED or SUBMITTED)
                 * @returns {{comments: *, endDate: *, days, employeeId: *, supervisorId: *, startDate: *, status: *}}
                 */
                $scope.getSendObject = function (statusType) {

                    $scope.data.days = $scope.data.days.sort(function (a, b) {
                        return a.date - b.date;
                    });
                    if (!Array.isArray($scope.data.comments)) {
                        $scope.data.comments = [];
                    }
                    if ($scope.data.addedComment !== "") {   
                        $scope.data.comments.push({
                                                      text: $scope.data.addedComment,
                                                      authorId: $scope.data.employeeId
                                                  });
                    }
                    return {
                        requestId: $scope.data.requestId,
                        status: statusType,
                        employeeId: $scope.empId,
                        supervisorId: $scope.supId,
                        startDate: $scope.data.days[0].date,
                        endDate: $scope.data.days[$scope.data.days.length - 1].date,
                        days: $scope.data.days,
                        comments: $scope.data.comments
                    };
                };

                /**
                 * Function to get the first day of each pay period from the list
                 * of days in a time off request. (The breaks in the display table
                 * will happen before these days)
                 */
                $scope.getFirstDatesInPayPeriods = function () {
                    $scope.data.days = $scope.data.days.sort(function (a, b) {
                        return a.date - b.date;
                    });

                    //variable to keep track of the total accruals used in the request at each day
                    var accrualsUsedSoFar = {
                        personal: 0,
                        vacation: 0,
                        sick: 0
                    };
                    //now have the pay periods in the request - find the first date in a pay period
                    if ($scope.data.days.length >= 0) {
                        $scope.data.days[0].firstDayInPeriod = true;
                        $scope.data.days.forEach(function(day, index) {
                            if (index === 0 || day.payPeriod !== $scope.data.days[index - 1].payPeriod) {
                                //this day starts a new pay period
                                day.firstDayInPeriod = true;
                                day.accruals = {
                                    'personal': 0,
                                    'vacation': 0,
                                    'sick': 0
                                };
                                //add in the projected accruals minus the accruals used in the request
                                var accrualArgs = {
                                    'empId': $scope.empId,
                                    'beforeDate': day.date.toISOString().substr(0, 10)
                                };

                                AccrualApi.get(accrualArgs).$promise.then(
                                    function (data) {
                                        day.accruals.personal = data.result.personalAvailable - accrualsUsedSoFar.personal;
                                        day.accruals.sick = data.result.sickAvailable - accrualsUsedSoFar.sick;
                                        day.accruals.vacation = data.result.vacationAvailable - accrualsUsedSoFar.vacation;
                                        accrualsUsedSoFar.personal -= day.personalHours;
                                        accrualsUsedSoFar.sick -= (day.sickEmpHours + day.sickFamHours);
                                        accrualsUsedSoFar.vacation -= day.vacationHours;
                                    });

                            } else {
                                //not the first day in the pay period, set to false and move on
                                day.firstDayInPeriod = false;
                                //These duplicate lines are here so that they don't execute before the API call finishes
                                accrualsUsedSoFar.personal -= day.personalHours;
                                accrualsUsedSoFar.sick -= (day.sickEmpHours + day.sickFamHours);
                                accrualsUsedSoFar.vacation -= day.vacationHours;
                            }
                        });
                    }
                };

                /**
                 * Function to save a time off request to the database. This is called
                 * when a user wants to save a request for later, and is not submitting
                 * the request yet.
                 */
                $scope.saveRequest = function () {
                    $scope.pageLoaded = false;
                    //validate request
                    if ($scope.validate()) {
                        console.log($scope.validationErrorMessages);
                        var sendObject = $scope.getSendObject("SAVED");
                        var endOfUrl = window.location.href.substring(window.location.href.length - 3, window.location.href.length);

                        //api call to save the request
                        updateRequestsApi.save(sendObject).$promise
                            .then(
                                //on success
                                function (data) {
                                    console.log("Success!");
                                    $scope.pageLoaded = false;
                                    var requestId = data.result.requestId;
                                    $scope.updateData(requestId);
                                    //go to the request's individual page if it was a new request
                                    if (endOfUrl === "new") {
                                        window.open(window.location.href.substring(0, window.location.href.length - 3) + requestId, "_self");
                                    }

                                    //update $scope.data to hold the request
                                    $scope.mode = "output";

                                },
                                //on failure
                                function (data) {
                                    console.log("There was an error while attempting to save your request.", data);
                                }
                            ).finally($scope.onloadFn());
                        //blue check "Saved!" at the top that fades
                    } else {
                        $scope.onloadFn();
                    }
                };

                /**
                 * Function to submit a time off request. This function is called
                 * when a user is ready to submit their time off request to their
                 * supervisor.
                 */
                $scope.submitRequest = function () {
                    //validate request
                    if ($scope.validate()) {
                        console.log($scope.validationErrorMessages);
                        $scope.pageLoaded = false;
                        var sendObject = $scope.getSendObject("SUBMITTED");
                        var endOfUrl = window.location.href.substring(window.location.href.length - 3, window.location.href.length);

                        //api call to submit the request
                        updateRequestsApi.save(sendObject).$promise
                            .then(
                                //on success
                                function (data) {
                                    console.log("Success!");
                                    $scope.pageLoaded = false;
                                    var requestId = data.result.requestId;
                                    //go to the request's individual page if it was a new request
                                    if (endOfUrl === "new") {
                                        window.open(window.location.href.substring(0, window.location.href.length - 3) + requestId, "_self");
                                    }

                                    //update $scope.data to hold the request
                                    $scope.updateData(requestId);
                                    $scope.mode = "output";
                                },
                                //on failure
                                function (data) {
                                    console.log("There was an error while attempting to submit your request.", data);
                                }
                            ).finally($scope.onloadFn());
                        //green check "Submitted to supervisor!" at the top that fades
                    }
                };

                /**
                 * This function is used to update the data held in scope.data for a particular request
                 * (denoted by requestId param) after it has been added to the database.
                 * (For example, the timestamp of a request is not added until the request is sent to the
                 * backend, so we need to update the front-end data to include the timestamp.)
                 *
                 * @param requestId
                 */
                $scope.updateData = function (requestId) {
                    RequestApi.get({requestId: requestId}).$promise.then(
                        //success
                        function (data) {
                            console.log("Successfully retrieved request #", requestId);
                            //Note: do not set $scope.data = data, as this will erase other attributes
                            //that have been assigned to $scope.data that aren't saved in the backend,
                            //such as "dateStr" for days in the request
                            $scope.data.requestId = data.requestId;
                            $scope.data.timestamp = data.timestamp;
                            $scope.onloadFn();
                        },
                        //failure
                        function (data) {
                            console.log("There was an error while retrieving request #", requestId, data);
                        }
                    );
                };
            }
        }
    }
})();