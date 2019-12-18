var essTime = angular.module('essTime');
/**
 * Service to run validation checks on time off request entries
 * General functionality: One function will be returned from the
 *      service. This function will call all other functions in
 *      the service class to perform validation, and will return
 *      a list of error messages based on validation that did not
 *      pass.
 */

essTime.factory('HolidayApi', ['$resource', function ($resource) {
    return $resource("/api/v1/holidays");
}]);

essTime.factory('EmploymentApi', ['$resource', function ($resource) {
    return $resource("/api/v1/employees/activeDates");
}]);

essTime.factory('EmpRequestApi', ['$resource', function ($resource) {
    return $resource("/api/v1/accruals/request/employee/:empId");
}]);

essTime.service('TimeOffRequestValidationService', ['HolidayApi', 'EmploymentApi',
                                                    'EmpRequestApi', timeOffRequestValidationUtils]);

function timeOffRequestValidationUtils(HolidayApi, EmploymentApi, EmpRequestApi) {

    return {
        runChecks: runChecks
    };

    /**
     *
     * @returns object with error codes and messages corresponding to each code
     */
    function errorMessages() {
        return {
            numDays: "ERROR: Your request must have at least one day.",
            nullDays: "ERROR: The DATE must be entered for all days in your request.",
            duplicateDate: "ERROR: There are duplicate dates in your request.",
            zeroHours: "ERROR: Each day must have more than 0 hours of time off requested.",
            positiveValues: "ERROR: The requested hours off are not all positive.",
            hoursPerDay: "ERROR: There are too many hours requested in one or more days.",
            hourInterval: "ERROR: Not all numeric values are in .25 intervals.",
            miscRequirement: "ERROR: Misc hours and misc type must either both be filled in or both be empty.",
            accrualUsage: "ERROR: You are requesting more time off than your accruals allow.",
            holidayHour: "ERROR: Holiday hours entered does not match the Senate's holiday schedule.",
            futureDates: "ERROR: Your request contains past dates.",
            empDates: "ERROR: Employee is not active for every date on the request: ", //include list of dates
            supDates: "ERROR: Supervisor is not active for every date on the request. ",
            dateExists: "ERROR: There are dates in your request that have already have time off approved in another request."
        };
    }

    /**
     * Function to run all other functions performing checks. This function
     * will be called outside of the class, and acts as a gateway to the validation
     * check functions.
     *
     * @param request - request object
     * @param accruals - total accruals available for the employee whose request
     *                   is being validated
     * @returns [array] - error messages corresponding to the checks that did not pass
     */
    function runChecks(request, accruals) {
        var returnErrorMessages = [];
        if (!numDaysCheck(request.days)) {
            returnErrorMessages.push(errorMessages().numDays);
        }
        if (!nullDaysCheck(request.days)) {
            returnErrorMessages.push(errorMessages().nullDays);
        }
        if (!duplicateDateCheck(request.days)) {
            returnErrorMessages.push(errorMessages().duplicateDate);
        }
        if (!moreThanZeroHoursCheck(request.days)) {
            returnErrorMessages.push(errorMessages().zeroHours);
        }
        if (!positiveValuesCheck(request.days)) {
            returnErrorMessages.push(errorMessages().positiveValues);
        }
        if (!hoursPerDayCheck(request.days)) {
            returnErrorMessages.push(errorMessages().hoursPerDay);
        }
        if (!hourIntervalCheck(request.days)) {
            returnErrorMessages.push(errorMessages().hourInterval);
        }
        if (!miscRequirementCheck(request.days)) {
            returnErrorMessages.push(errorMessages().miscRequirement);
        }
        if (!accrualUsageCheck(accruals, request.days)) {
            returnErrorMessages.push(errorMessages().accrualUsage);
        }
        if (!holidayHourCheck(request.days, request.startDate, request.endDate)) {
            returnErrorMessages.push(errorMessages().holidayHour);
        }
        if (!futureDatesCheck(request.days)) {
            returnErrorMessages.push(errorMessages().futureDates);
        }
        if (!supervisorCheck(request.empId, request.supId, request.days)) {
            returnErrorMessages.push(errorMessages().supDates);
        }
        // var unemployedDates = employedForAllDatesCheck(request.empId, request.days);
        // if (unemployedDates.length > 0) {
        //     var errMsg = errorMessages().empDates;
        //     for (var dayIndex = 0; dayIndex < unemployedDates.length - 1; dayIndex++) {
        //         errMsg = errMsg + unemployedDates[dayIndex].date + ", ";
        //     }
        //     errMsg = errMsg + unemployedDates[unemployedDates.length - 1];
        //     returnErrorMessages.push(errMsg);
        // }
        if (!daysExistAlreadyCheck(request.days, request.empId)) {
            returnErrorMessages.push(errorMessages().dateExists);
        }
        return returnErrorMessages;
    }

    /**
     * Function to check that the request has at least one day
     *
     * @param days - list of day objects from a time off request
     * @returns True - days.length > 0
     *         False - Otherwise
     */
    function numDaysCheck(days) {
        return days.length > 0;
    }

    /**
     * Function to check that all days are selected from the date picker (i.e., no days
     * are null)
     *
     * @param days - list of day objects from a time off request
     * @returns True - all days are selected
     *         False - otherwise (there are null values)
     */
    function nullDaysCheck(days) {
        var passNullDaysCheck = true;
        days.forEach(function (day) {
            if (day.date === null) {
                passNullDaysCheck = false;
            }
        });
        return passNullDaysCheck;
    }

    /**
     * Function to check that no two dates are the same in a request
     *
     * @param days - list of day objects from a time off request
     * @returns True - all dates are unique
     *         False - otherwise
     */
    function duplicateDateCheck(days) {
        //issue using sets here
        var daysSet = new Set();
        days.forEach(function (day) {
            daysSet.add(day.date.toDateString()); //.toDateString() is used to remove the
        });                                       // timestamp when considering if two dates
        return days.length === daysSet.size;      // are equal
    }

    /**
     * Function to check that there are more than 0 hours for each day in a request
     *
     * @param days - list of day objects from a time off request
     *
     *@returns True - all days have > 0 hours
     *         False - otherwise
     */
    function moreThanZeroHoursCheck(days) {
        var moreThanZeroHours = true;
        days.forEach(function (day) {
            if (day.totalHours <= 0) {
                moreThanZeroHours = false;
            }
        });
        return moreThanZeroHours;
    }

    /**
     * Function to check that all numeric entries are positive
     *
     * @param days - list of day objects from a time off request
     * @returns True - if all numeric entries are >= 0
     *         False - otherwise
     */
    function positiveValuesCheck(days) {
        var onlyPositiveValues = true;
        days.forEach(function (day) {
            if (day.holidayHours < 0 || day.miscHours < 0 || day.personalHours < 0 ||
                day.sickEmpHours < 0 || day.sickFamHours < 0 || day.vacationHours < 0 || day.workHours < 0) {
                onlyPositiveValues = false;
            }
        });
        return onlyPositiveValues;
    }

    /**
     *Function to check that one day does not have a totalHours value greater than 24
     *
     * @param days - list of day objects from a time off request
     * @returns True - all days have totals less than 24 hours
     *         False - otherwise
     */
    function hoursPerDayCheck(days) {
        var allDaysUnder24Hours = true;
        days.forEach(function (day) {
            if (day.totalHours > 24) {
                allDaysUnder24Hours = false;
            }
        });
        return allDaysUnder24Hours;
    }

    /**
     * Function to check that all entries for hours are in .25 intervals
     *
     * @param days - list of day objects from a time off request
     * @returns Boolean - True - all entries are of .25 hour intervals
     *                    False - otherwise
     */
    function hourIntervalCheck(days) {
        return true;
    }

    /**
     * Function to check that misc hours is greater than zero if and only if
     * a misc_type is selected
     *
     * @param days - list of day objects from a time off request
     * @returns True - no misc type violations
     *         False - otherwise
     */
    function miscRequirementCheck(days) {
        var miscRequirementSatisfied = true;
        days.forEach(function (day) {
            if ((day.miscType === null && day.miscHours > 0) ||
                (day.miscType !== null && day.miscHours === 0)) {
                miscRequirementSatisfied = false;
            }
        });
        return miscRequirementSatisfied;
    }

    /**
     * Function to check if sum of accruals used is less than or equal to the
     * accruals available
     *
     * @param accruals - An object with string values for each accrual type and
     *                    corresponding floats for the available hours of that type
     *                    ex) accruals = {
     *                        vacation: 100,
     *                        sick: 76,
     *                        personal: 34
     *                    }
     * @param days - list of day objects from a time off request
     * @returns True - accruals requested are available
     *         False - otherwise
     */
    function accrualUsageCheck(accruals, days) {
        var vacationRequested = 0,
            personalRequested = 0,
            sickRequested = 0;
        days.forEach(function (day) {
            vacationRequested += day.vacationHours;
            personalRequested += day.personalHours;
            sickRequested += day.sickEmpHours + day.sickFamHours;
        });
        return (vacationRequested <= accruals.vacation) &&
            (personalRequested <= accruals.personal) &&
            (sickRequested <= accruals.sick);
    }

    /**
     * Function to check if holiday hours entered differs from the Senate Holiday hours
     *
     * @param days - list of day objects from a time off request
     * @param startDate - the earliest date on the request
     * @param endDate - the latest date on the request
     * @Return True - holiday hours match the Senate Holiday hours
     *         False - otherwise
     */
    function holidayHourCheck(days, startDate, endDate) {
        var holidayCheck = true;
        days = days.sort(function(a,b) {
            return a.date - b.date;
        });
        var holidays;
        var params = {
            'fromDate': days[0].date.toISOString().substr(0,10),
            'toDate': days[days.length-1].date.toISOString().substr(0,10)
        };
        HolidayApi.get(params).$promise.then(
            function (data) {
                holidays = data.holidays;
                //compare holidays and holiday hours on days in the request

            }, function (data) {
                console.log("There was an error retrieving holiday data: ", data);
            }
        );

        return holidayCheck;
    }

    /**
     * Function to check that all dates in a request are either current day or a future day
     *
     * @param days - list of day objects from a time off request
     * @Return True - all dates occur in the future
     *         False - otherwise
     */
    function futureDatesCheck(days) {
        var allDatesInFuture = true;
        var today = new Date();
        days.forEach(function (day) {
            if (day.date !== null) {
                if (day.date.getDate() < today.getDate()) {
                    allDatesInFuture = false;
                }
            }
        });
        return allDatesInFuture;
    }

    /**
     * Function to check that all dates are between an employees start and end date
     *
     * @param empId - integer for employee ID
     * @param days - list of day objects from a time off request
     * @returns [] - all dates are in the valid employment range
     *         list of all dates not in employment range - otherwise
     */
    function employedForAllDatesCheck(empId, days) {
        var unemployedDays = [];
        var empDates = [];
        var params = {
            'empId': empId
        };
        function apiCall() {
            EmploymentApi.get(params).$promise.then(
                function(data) {
                    empDates = data.activeDates.activeDates;
                    days.forEach(function(day) {
                        var currentDayExists = false;
                        empDates.forEach(function(range) {
                            var beginYear = parseInt(range.beginDate.substring(0,4));
                            var beginMonth = parseInt(range.beginDate.substring(5,7)) - 1;
                            var beginDay = parseInt(range.beginDate.substring(8,10));
                            var beginDate = new Date(beginYear, beginMonth, beginDay);
                            var endYear = parseInt(range.endDate.substring(0,4));
                            var endMonth = parseInt(range.endDate.substring(5,7)) - 1;
                            var endDay = parseInt(range.endDate.substring(8,10));
                            var endDate = new Date(endYear, endMonth, endDay);
                            if(day.date >= beginDate && day.date <= endDate) {
                                currentDayExists = true;
                            }
                        });
                        if(!currentDayExists) {
                            unemployedDays.push(day.date);
                        }
                    });
                    console.log(unemployedDays);
                },
                function(data) {
                    console.log("There was an error accessing employee active dates: ", data);
                }
            );
        }
        return unemployedDays;
    }

    /**
     * Function to check that all dates are under the same supervisor
     *
     * @param empId - integer for employee ID
     * @param supId - integer for supervisor ID
     * @param days - list of day objects from a time off request
     * @returns boolean - true - all dates are under the same supervisor
     *                    false - otherwise
     */
    function supervisorCheck(empId, supId, days) {
        // days = days.sort(function(a,b) {
        //     return a.date - b.date;
        // });
        // var params = {
        //     supId: supId,
        //     'fromDate': days[0].date.toISOString().substr(0,10),
        //     'toDate': days[days.length-1].date.toISOString().substr(0,10)
        // };
        // SupervisorDateApi.get(params).$promise.then(
        //     function(data){
        //         console.log("Data: ", data);
        //     },
        //     function (data) {
        //         console.log("Error Accessing supervisor info, ", data);
        //     }
        // );
        return true;
    }

    /**
     * Function to check that none of the days being requested for
     * time off appear in other time off requests
     *
     * @param days - a list of days in the current request
     * @param empId - the id of the employee whose request days we will be
     *                looking for
     * @returns boolean - true - no date in the current request duplicates a
     *                           date from an old request
     *                    false - there exists a date in the current request
     *                    that is already in another request for this employee
     */
    function daysExistAlreadyCheck(days, empId) {
        var duplicateDateInAnotherRequest = false;
        var datesInPreviousRequests = [];
        //make use of EmpRequestsApi
        //start date should be when the last pay period started (
        // i.e., the last time sheet they can modify)
        //get the start date of the previous pay period
        // - use the api on 14 days before today,
        //User could be asking for time off in the past
        var today = new Date();
        var twoWeeksAgo = new Date(today.getTime() - 14*24*60*60*1000);
        var params = {
            empId: empId,
            startRange: twoWeeksAgo.toISOString().substr(0,10),
            endRange: ""
        };
        EmpRequestApi.query(params).$promise.then(
            function (data) {
                //get the dates from the request and add them to master list
                data.forEach(function(request) {
                    request.days.forEach(function(day) {
                        datesInPreviousRequests.push(day.date)
                    })
                });
                console.log(datesInPreviousRequests);
            },
            function (data) {
                console.log("There was an error attempting to get request data for the employee: ", data);
            }
        ).finally(
            days.forEach(function(day) {
                var date = day.date.toISOString().substr(0,10);
                console.log(date);
                if(datesInPreviousRequests.indexOf(date) >= 0) {
                    duplicateDateInAnotherRequest = true;
                }
            })
        );

        return !duplicateDateInAnotherRequest;
    }

}

