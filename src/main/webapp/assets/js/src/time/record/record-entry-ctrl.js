angular.module('essTime')
        .controller('RecordEntryController', ['$scope', '$rootScope', '$filter', '$q', 'appProps',
                                              'ActiveTimeRecordsApi', 'TimeRecordApi',
                                              'AccrualPeriodApi', 'ExpectedHoursApi',
                                              'AllowanceApi', 'MiscLeaveGrantApi', 'HolidayApi', 'TimeRecordCreationApi',
                                              'activeTimeEntryRow', 'RecordUtils', 'LocationService', 'modals',
                                              'promiseUtils', 'AllowanceUtils',
                                              recordEntryCtrl])
;

function recordEntryCtrl($scope, $rootScope, $filter, $q, appProps,
                         activeRecordsApi, recordSaveApi,
                         accrualPeriodApi, expectedHoursApi,
                         allowanceApi, miscLeaveGrantApi, holidayApi,
                         recordCreationApi, activeRow, recordUtils, locationService, modals,
                         promiseUtils, allowanceUtils) {

    function getInitialState() {
        function shortnameMap (miscLeaves) {
            var map = {}
            for (var leaveIndex in miscLeaves) {
                var miscLeave = miscLeaves[leaveIndex];
                map[miscLeave.type] = miscLeave.shortName
            }
            return map
        }

        return {
            empId: appProps.user.employeeId,    // Employee Id
            miscLeaves: appProps.miscLeaves,    // Listing of misc leave types
            miscLeaveGrantInfoList: null,       // List of info about grants of a misc leave type for the currently selected record
            miscLeaveUsageError: null,          // Data on misc leave that too much is being used of
            accrual: null,                      // Accrual info for selected record
            expectedHrs: null,                  // An object containing expected hour data for the selected record
            allowances: {},                     // A map that stores yearly temp employee allowances
            selectedYear: 0,                    // The year of the selected record (makes it easy to get the selected record's allowance)
            records: [],                        // All active employee records
            iSelectedRecord: 0,                 // Index of the currently selected record,
            salaryRecs: [],                     // A list of salary recs that are active during the selected record's date range
            iSelSalRec: 0,                      // Index of the selected salary rec (used when there is a salary change mid record)
            tempEntries: false,                 // True if the selected record contains TE pay entries
            annualEntries: false,               // True if the selected record contains RA or SA entries
            totals: {},                         // Stores record wide totals for time entry fields of the selected record
            holidays: null,                     // Stores a map of holidays

            request: {                          // Flags indicating if ajax requests are in progress
                records: false,                 //  Get active records
                accruals: false,                //  Get accruals for selected record
                allowances: false,              //  Get allowances for selected record
                save: false                     //  Save selected record
            },
            // A map for displaying convenience.
            miscLeavesShortnameMap: shortnameMap(appProps.miscLeaves),
        }
    }

    // Record error codes that indicate that there was a third party modification
    var modifiedErrorCodes = [2, 7];

    $scope.state = null;                  // The container for all the state variables for this page

    // Create a new state from the values in the default state.
    $scope.initializeState = function() {
        $scope.state = getInitialState();
    };

    $scope.init = function() {
        //console.log('Time record initialization');
        $scope.initializeState();
        $scope.getRecords();
        $scope.getHolidays();
    };

    /** --- Watches --- */

    // Update accruals, display entries, totals when a new record is selected
    $scope.$watchGroup(['state.records', 'state.iSelectedRecord'], function() {
        if ($scope.state.records && $scope.state.records[$scope.state.iSelectedRecord]) {
            detectPayTypes();
            $q.all([    // Get Accruals/Allowances for the record and then call record update methods
                $scope.getAccrualForSelectedRecord(),
                $scope.getAllowanceForSelRecord(),
                $scope.getExpectedHoursForSelRecord()
            ]).then(function() {
                //console.log('allowances/accruals retrieved, calling update functions');
                getSelectedSalaryRecs();
                onRecordChange();
                setRecordSearchParams();
                fullValidationCheck();
            });
            $scope.getMiscLeaveTypeGrants();
        }
    });

    /** --- API Methods --- */

    /**
     * Fetches the employee's active records from the server, auto-selecting a record
     * if it's end date is supplied in the query params.
     */
    $scope.getRecords = function() {
        $scope.initializeState();
        
        $scope.state.request.records = true;
        var params = { empId: $scope.state.empId };
        activeRecordsApi.get(params,
            function (response) {
                if ($scope.state.empId in response.result.items) {
                    $scope.state.allRecords = response.result.items[$scope.state.empId];
                    $scope.state.records = $scope.state.allRecords
                        .filter(function (record) {return record.scope === 'E';});
                    angular.forEach($scope.state.records, function(record, index) {
                        // Compute the due from dates for each record
                        var endDateMoment = moment(record.endDate).add(1, 'days').startOf('day');
                        record.dueFromNowStr = endDateMoment.fromNow(false);
                        record.isDue = endDateMoment.isBefore(moment());
                        // Set the record index
                        record.index = index;
                        // Assign indexes to the entries
                        angular.forEach(record.timeEntries, function(entry, i) {
                            entry.index = i;
                        });
                        // Set initial comment
                        record.initialRemarks = record.remarks;
                    });
                    linkRecordFromQueryParam();
                }
            }, $scope.handleErrorResponse)
            .$promise.finally(function() {
                $scope.state.request.records = false;
            });
    };

    /**
     * Validates and saves the currently selected record.
     * @param submit - Set to true if user is also submitting the record. This will modify the record status if
     *                 it completes successfully.
     */
    $scope.saveRecord = function(submit) {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        console.log(submit ? 'submitting' : 'saving', 'record', record);
        fullValidationCheck();
        var entryErrors = $scope.selRecordHasEntryErrors();
        if (entryErrors || submit && $scope.selRecordHasRecordErrors()) {
            $scope.$broadcast('validateRecordEntries');
            return;
        }

        // Promise that encompasses any pre-save confirmations
        var confirmPromise = $q.when();

        // Perform some additional checks and confirmations if submitting
        if (submit) {
            confirmPromise =
                promiseUtils.serial(getSubmitDialogs())
                .then(function () {
                    return modals.open('submit-ack', {'record': record}, true);
                })
        }

        var saveSuccess = false;

        // Chain saving promises after the confirm promise
        confirmPromise
            .then(function () { return saveRecord(submit) })
            .then(function () { saveSuccess = true; return $q.when();})
            .then(function () { return modals.open(submit ? 'post-submit' : 'post-save');})
            .then($scope.init, function () {
                // Only logout if there was a successful save
                if (saveSuccess) {
                    locationService.logout();
                }
            });
    };

    /**
     * Saves or submits the currently selected record.
     * This assumes any necessary validation has already been
     * made on this record.
     * @param submit - true if the record is to be submitted
     */
    function saveRecord (submit) {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        $scope.state.request.save = true;
        modals.open(submit ? 'submit-progress' : 'save-progress');
        return recordSaveApi.save({action: submit ? 'submit' : 'save'}, record,
            function (resp) {
                modals.resolve();
            }, function (resp) {
                modals.reject();

                // Check the error codes to see if any of them indicate an external modification
                var errorMap = (((resp || {}).data || {}).errorData || {}).items || {};
                var errorCodes = Object.keys(errorMap).map(parseInt);
                var modified = errorCodes.filter(function (errorCode) {
                    return modifiedErrorCodes.indexOf(errorCode) >= 0;
                }).length > 0;

                // If the error could be explained by an external modification,
                // prompt the user to refresh records and resubmit
                // Otherwise open the error modal
                if (modified) {
                    modals.open('record-modified-dialog')
                        .then($scope.init)
                        .catch($scope.handleErrorResponse);
                } else {
                    $scope.handleErrorResponse(resp);
                }
            }).$promise.finally(function () {
                $scope.state.request.save = false;
        });
    }

    /**
     * Fetches the accruals for the currently selected time record from the server.
     * @returns Promise that is fulfilled when the accruals are received
     */
    $scope.getAccrualForSelectedRecord = function() {
        if ($scope.state.annualEntries) {
            var empId = $scope.state.empId;
            var record = $scope.state.records[$scope.state.iSelectedRecord];
            var periodStartMoment = moment(record.payPeriod.startDate);
            $scope.state.request.accruals = true;
            return accrualPeriodApi.get({
                empId: empId,
                beforeDate: periodStartMoment.format('YYYY-MM-DD')
            }, function (resp) {
                if (resp.success) {
                    $scope.state.accrual = resp.result;
                }
            }, $scope.handleErrorResponse)
            .$promise.finally(function() {
                $scope.state.request.accruals = false;
            });
        }
        // Return an automatically resolving promise if no request was made
        return $q(function (resolve) {resolve()});
    };

    /**
     * Get the expected hours for the currently selected record
     * @returns {*}
     */
    $scope.getExpectedHoursForSelRecord = function () {
        // Return an automatically resolving promise if no annual entries exist
        if (!$scope.state.annualEntries) {
            return $q(function (resolve) {resolve()});
        }
        var empId = $scope.state.empId;
        var record = $scope.getSelectedRecord();
        var params = {
            empId: empId,
            beginDate: record.beginDate,
            endDate: record.endDate
        };
        $scope.state.request.expectedHrs = true;
        return expectedHoursApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(cleanUp);

        function onSuccess (resp) {
            $scope.state.expectedHrs = resp.result;
        }
        function cleanUp() {
            $scope.state.request.expectedHrs = false;
        }
    };

    /**
     * Gets the allowance state for the year of the selected record, if it hasn't already been retrieved
     * @returns Promise that is fulfilled when the allowances are received
     */
    $scope.getAllowanceForSelRecord = function() {
        var record = $scope.getSelectedRecord();
        $scope.state.selectedYear = moment(record.beginDate).year();
        if ($scope.state.tempEntries && !$scope.state.allowances.hasOwnProperty($scope.state.selectedYear)) {
            var params = {
                empId: $scope.state.empId,
                year: $scope.state.selectedYear
            };
            $scope.state.request.allowances = true;
            return allowanceApi.get(params, function(response) {
                for (var i in response.result) {
                    var allowance = response.result[i];
                    //console.log('retrieved allowance', allowance.empId, allowance.year);
                    $scope.state.allowances[allowance.year] = allowance;
                }
            }, $scope.handleErrorResponse)
            .$promise.finally(function () {
                $scope.state.request.allowances = false;
            });
        }
        // Return an automatically resolving promise if no request was made
        return $q(function (resolve) {resolve()});
    };

    $scope.getMiscLeaveTypeGrants = function () {
        var params = {empId: $scope.state.empId, endDateStr:
            $scope.state.records[$scope.state.iSelectedRecord].beginDate};
        miscLeaveGrantApi.get(params, function (response) {
            $scope.state.miscLeaveGrantInfoList = response.result;
        }, $scope.handleErrorResponse);
    };

    $scope.getHolidays = function () {
        var params = {
            fromDate: moment().subtract(1, 'year').format('YYYY-MM-DD'),
            toDate: moment().add(1, 'year').format('YYYY-MM-DD')
        };
        holidayApi.get(params, function (response) {
            $scope.state.holidays = {};
            angular.forEach(response.holidays, function (holiday) {
                if (!holiday.unofficial) {
                    $scope.state.holidays[holiday.date] = holiday;
                }
            });
            //console.log('retrieved holidays');
        }, $scope.handleErrorResponse);
    };

    $scope.createNextRecord = function () {
        if (!$scope.canCreateNextRecord()) {
            return;
        }
        var latestRecord = getLatestRecord();
        var nextRecBeginDate = moment(latestRecord.endDate).add(1, 'day');
        $scope.state.request.records = true;
        var params = {
            empId: $scope.state.empId,
            date: nextRecBeginDate.format('YYYY-MM-DD')
        };
        //console.log(params);
        recordCreationApi.save(params, {}, function (response) {
            $scope.init();
        }, function (errorResponse) {
            $scope.handleErrorResponse(errorResponse);
            $scope.state.request.records = false;
        })
    };

    /** --- Display Methods --- */

    /**
     * Returns the currently selected record.
     * @returns timeRecord object
     */
    $scope.getSelectedRecord = function() {
        return $scope.state.records[$scope.state.iSelectedRecord];
    };

    /**
     * Closes any open modals by resolving them.
     */
    $scope.resolveModal = function (result) {
        modals.resolve(result);
    };

    /**
     * Closes any open modals by rejecting them.
     */
    $scope.rejectModal = function (reason) {
        modals.reject(reason);
    };


    /**
     * Returns true if the given date falls on a weekend.
     * @param date - ISO, JS, or Moment Date
     * @returns {boolean} - true if weekend, false otherwise.
     */
    $scope.isWeekend = function(date) {
        return $filter('momentIsDOW')(date, [0, 6]);
    };

    /**
     * This method is called every time a field is modified on the currently selected record.
     */
    $scope.setDirty = function(entry) {
        $scope.state.records[$scope.state.iSelectedRecord].dirty = true;
        if (entry) {
            entry.dirty = true;
        }
        onRecordChange();
    };

    /**
     * Return true if the selected record is valid, i.e. it exists, and all entries are valid
     * @returns {boolean}
     */
    $scope.recordValid = function () {
        var record = $scope.getSelectedRecord();
        return !(record == null || $scope.selRecordHasEntryErrors() );
    };

    $scope.isRecordEmpty = function (record) {
        var timeEntries = record.timeEntries;
        var nullRecordCount = 0;

        for (var i = 0; i < timeEntries.length; i++) {
            if ( (timeEntries[i].payType === "RA" || timeEntries[i].payType === "SA") &&
                timeEntries[i].totalHours === 0) {
                nullRecordCount = nullRecordCount + 1;
                if (timeEntries[i].workHours !== null || timeEntries[i].travelHours !== null
                    || timeEntries[i].holidayHours !== null || timeEntries[i].vacationHours !== null
                    || timeEntries[i].personalHours !== null || timeEntries[i].sickEmpHours !== null
                    || timeEntries[i].sickFamHours !== null || timeEntries[i].miscHours !== null)  {
                    nullRecordCount = nullRecordCount - 1;
                }
            }
        }
        if (nullRecordCount === timeEntries.length) {
            //RECORD IS EMPTY RETURN TRUE
            return true;
        }
        //RECORD HAS SOME DATA RETURN FALSE
        return false;
    }

    /**
     * Returns true if the record is submittable, i.e. it exists, passes all validations, and has ended or will end
     * today.
     * @returns {boolean}
     */
    $scope.recordSubmittable = function () {
        return !$scope.requestInProgress() &&
               $scope.recordValid() &&
               !$scope.selRecordHasRecordErrors() &&
            !$scope.isRecordEmpty($scope.getSelectedRecord());
    };

    /**
     * Get the number of available work hours at the selected salary rate
     *  such that the record cost does not exceed the employee's annual allowance
     * @returns {number}
     */
    $scope.getAvailableHours = function() {
        var allowance = $scope.state.allowances[$scope.state.selectedYear];
        var tempWorkHours = $scope.state.totals.tempWorkHours;

        return allowanceUtils.getAvailableHours(allowance, tempWorkHours);
    };

    /**
     *
     * @param salaryRec
     * @returns {string}
     */
    $scope.getSalRecDateRange = function(salaryRec) {
        var record = $scope.getSelectedRecord();
        var beginDate = moment(salaryRec.effectDate).isAfter(record.beginDate) ? salaryRec.effectDate : record.beginDate;
        var endDate = moment(salaryRec.endDate).isAfter(record.endDate) ? record.endDate : salaryRec.endDate;
        return moment(beginDate).format('M/D') + ' - ' + moment(endDate).format('M/D');
    };


    /**
     * Get the start date of the given salary rec with respect to the selected record
     * @param salaryRec
     * @returns {Date}
     */
    $scope.getSalRecStartDate = function(salaryRec) {
        var record = $scope.getSelectedRecord();
        return moment(salaryRec.effectDate).isAfter(record.beginDate) ? salaryRec.effectDate : record.beginDate;
    };

    /**
     * Get the start date of the given salary rec with respect to the selected record
     * @param salaryRec
     * @returns {Date}
     */
    $scope.getSalRecEndDate = function(salaryRec) {
        var record = $scope.getSelectedRecord();
        return moment(salaryRec.endDate).isAfter(record.endDate) ? record.endDate : salaryRec.endDate;
    };

    /**
     * Returns a formatted string displaying the date range of the given record
     * @param record
     * @returns {string}
     */
    $scope.getRecordRangeDisplay = function (record) {
        return moment(record.beginDate).format('l') + ' - ' + moment(record.endDate).format('l');
    };

    /**
     * Set the selected record as having been focused when an entry validate event is caught
     */
    $rootScope.$on('validateRecordEntries', function() {
        var record = $scope.getSelectedRecord();
        record.focused = true;
    });

    $scope.isFieldSelected = function (entry, fieldName) {
        var fieldIdSelector = '#' + entry.date + '-' + fieldName;
        return angular.element(fieldIdSelector).is(':focus');
    };

    /**
     * Functions that determine the tab index for accrual hours
     */
    $scope.accrualTabIndex = {
        holiday: getAccrualTabIndexFn('holidayHours'),
        vacation: getAccrualTabIndexFn('vacationHours'),
        personal: getAccrualTabIndexFn('personalHours'),
        sickEmp: getAccrualTabIndexFn('sickEmpHours'),
        sickFam: getAccrualTabIndexFn('sickFamHours'),
        misc: getAccrualTabIndexFn('miscHours')
    };

    function getAccrualTabIndexFn (propName) {
        return function (entry) {
            if (entry[propName] !== null && $scope.getSelectedRecord().focused) return 1;
            if (entry.total < 7 && !$scope.isWeekend(entry.date)) return 1;
            if ($scope.isFieldSelected(entry, propName)) return 1;
            return -1;
        }
    }

    /**
     * Checks if the hour total of the annual entries for the selected record
     * is greater than or equal to the biweekly expected hours for the selected pay period
     * @returns {boolean}
     */
    $scope.expectedHoursEntered = function() {
        if (!$scope.state.annualEntries) {
            return true;
        }
        return $scope.state.expectedHrs.periodHoursExpected <= $scope.state.totals.raSaTotal;
    };

    /**
     * Checks if the hour total of the annual entries for the selected record
     * is greater than or equal to the biweekly expected hours for the selected pay period
     * @returns {boolean}
     */
    $scope.futureEndDate = function() {
        var record = $scope.getSelectedRecord();

        return moment(record.endDate).isAfter(moment(), 'day');
    };


    /**
     * Return a misc leave predicate function that will determine if a misc leave can be used on the given date
     * @param date
     * @returns {Function}
     */
    $scope.getMiscLeavePredicate = function(date) {
        var dateMoment = moment(date);
        return function(miscLeave) {
            // Return true if the misc leave is not restricted
            if (miscLeave.restricted === false) {
                return true;
            }
            var grantInfoList = $scope.state.miscLeaveGrantInfoList
            for (var grantIndex in grantInfoList) {
                var grant = grantInfoList[grantIndex].grant;
                // Return true if the date falls within the grant date range and is of the same leave type
                if (dateMoment.isBefore(grant.beginDate, 'day') || dateMoment.isAfter(grant.endDate, 'day')) {
                    continue;
                }
                if (miscLeave.type === grant.miscLeaveType) {
                    return true;
                }
            }
            return false;
        }
    };

    /**
     * Returns true iff the given entry is a holiday
     * @param entry
     * @returns {boolean}
     */
    $scope.isHoliday = function (entry) {
        return $scope.state.holidays && $scope.state.holidays.hasOwnProperty(entry.date);
    };

    /**
     * Return the number of holiday hours allotted for the given date
     * Return 7 if the holidays have not yet been loaded to prevent error flickering
     * @param entry
     * @returns {number}
     */
    $scope.getHolidayHours = function (entry) {
        if (!$scope.state.holidays) { // Return the max holiday hours if holidays have not yet loaded
            return 7;
        }
        return $scope.isHoliday(entry) ? $scope.state.holidays[entry.date].hours : 0;
    };

    /**
     * Return true if the employee is eligible to create a new time record for the next period
     * @returns {boolean}
     */
    $scope.canCreateNextRecord = function () {
        if ($scope.state.records.length > 0) {
            return false;
        }

        // Return false if any existing record has a begin date past the current date
        for (var iRecord in $scope.state.allRecords) {
            if (!$scope.state.allRecords.hasOwnProperty(iRecord)) {
                continue;
            }

            var record = $scope.state.allRecords[iRecord];
            if (moment().isBefore(record.beginDate)) {
                return false;
            }
        }

        var latestRecord = getLatestRecord();
        // Do not allow next record creation if the latest record does not cover the current day
        // This would indicate a time record manager failure
        if (latestRecord === null || moment().isAfter(latestRecord.endDate, 'day')) {
            return false;
        }

        return true;
    };

    /**
     * Return true if a request is in progress
     */
    $scope.requestInProgress = function () {
        for (var iReq in $scope.state.request) {
            if (!$scope.state.request.hasOwnProperty(iReq)) {
                continue;
            }
            if ($scope.state.request[iReq] === true) {
                return true;
            }
        }
        return false;
    };

    /** --- Internal Methods --- */

    /**
     * Refreshes totals and validates a record when a change occurs on a record.
     */
    function onRecordChange() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        // Todo delay sanitation to allow entry of .25 and .75
        // sanitizeEntries(record);
        recordUtils.calculateDailyTotals(record);
        $scope.state.totals = recordUtils.getRecordTotals(record);
    }

    /**
     * Ensure that all time entered is in multiples of 0.25 or 0.5 for Temporary and Annual entries respectively
     * @param record
     */
    function sanitizeEntries(record) {
        var timeEntryFields = recordUtils.getTimeEntryFields();
        angular.forEach(record.timeEntries, function (entry) {
            var validInterval = isTemporaryEmployee(entry) ? 0.25 : 0.5;
            var inverse = 1 / validInterval;
            angular.forEach(timeEntryFields, function(fieldName) {
                var value = entry[fieldName];
                if (value) {
                    entry[fieldName] = Math.round((value - Math.abs(value) % validInterval) * inverse) / inverse
                }
            })
        });
    }

    /**
     * Iterates through the entries of the currently selected record,
     * setting the state to indicate if the record has TE pay entries, RA/SA entries or both
     * @param record
     */
    function detectPayTypes() {
        $scope.state.tempEntries = $scope.state.annualEntries = false;
        if ($scope.state.records.length > 0) {
            var record = $scope.getSelectedRecord();
            for (var iEntry in record.timeEntries) {
                var entry = record.timeEntries[iEntry];
                if (isTemporaryEmployee(entry)) {
                    $scope.state.tempEntries = true;
                } else if (isSalariedEmployee(entry)) {
                    $scope.state.annualEntries = true;
                }
            }
        }
    }

    /**
     * Adds all salaryRecs relevant to the selected record to the salaryRecs state object
     */
    function getSelectedSalaryRecs() {
        var salaryRecs = [];
        $scope.state.salaryRecs = salaryRecs;
        $scope.state.iSelSalRec = 0;
        if (!$scope.state.tempEntries) return;
        var allowance = $scope.state.allowances[$scope.state.selectedYear];
        var record = $scope.getSelectedRecord();
        var highestRate = 0;
        angular.forEach(allowance.salaryRecs, function (salaryRec) {
            // Select only temporary salaries that are effective during the record date range
            if (salaryRec.payType === 'TE' &&
                    !moment(salaryRec.effectDate).isAfter(record.endDate) &&
                    !moment(record.beginDate).isAfter(salaryRec.endDate)) {
                salaryRecs.push(salaryRec);
                if (salaryRec.salaryRate > highestRate) {
                    highestRate = salaryRec.salaryRate;
                    $scope.state.iSelSalRec = allowance.salaryRecs.indexOf(salaryRec);
                }
            }
        });

        allowanceUtils.computeRemaining(allowance, record);
    }

    function getLatestRecord() {
        var latestRecord = null;
        angular.forEach($scope.state.allRecords, function (record) {
            if (!latestRecord || moment(record.beginDate).isAfter(latestRecord.beginDate)) {
                latestRecord = record;
            }
        });
        return latestRecord;
    }

    /**
     * Recursively ensures that all boolean fields are false within the given object.
     * @param object
     * @returns {boolean}
     */
    function allFalse(object) {
        if (typeof object === 'boolean') {
            return object;
        }
        for (var prop in object) {
            if (object.hasOwnProperty(prop) && allFalse(object[prop])) {
                return false;
            }
        }
        return true;
    }

    /**
     * Sets the search params to indicate the currently active record.
     */
    function setRecordSearchParams() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        locationService.setSearchParam('record', record.beginDate);
    }

    /**
     * Checks for a 'record' search param in the url and if it exists, the record with a start date that matches
     * the given date will be set as the selected record.
     */
    function linkRecordFromQueryParam() {
        var recordParam = locationService.getSearchParam('record');
        if (recordParam) {
            // Need to break out early, hence no angular.forEach.
            for (var iRecord in $scope.state.records) {
                var record = $scope.state.records[iRecord];
                if (record.beginDate === recordParam) {
                    $scope.state.iSelectedRecord = parseInt(iRecord);
                    break;
                }
            }
        }
    }

    function isTemporaryEmployee(entry) {
        return entry.payType === 'TE';
    }

    function isSalariedEmployee(entry) {
        return entry.payType === 'RA' || entry.payType === 'SA';
    }


    function getSubmitDialogs() {
        var submitDialogs = [];

        var prevUnsubmittedTe = getPrevUnsubmittedTe();
        if (prevUnsubmittedTe && prevUnsubmittedTe.length > 0) {
            submitDialogs.push(function () {
                return modals.open("unsubmitted-te-warning", {records: prevUnsubmittedTe}, true);
            })
        }

        if (!$scope.expectedHoursEntered()) {
            submitDialogs.push(function () {
                return modals.open("expectedhrs-dialog", {
                    serviceYtd: $scope.state.accrual.serviceYtd,
                    serviceYtdExpected: $scope.state.accrual.serviceYtdExpected,
                    recordHrsExpected: $scope.state.expectedHrs.periodHoursExpected,
                    raSaTotal: $scope.state.totals.raSaTotal
                }, true);
            });

        }

        if ($scope.futureEndDate()) {
            submitDialogs.push(function () {
                return  modals.open("futureenddt-dialog", {}, true);
            });
        }

        return submitDialogs
    }

    /** --- Validation --- **/

    /**
     * Runs a full validation check on the selected record
     * Setting error flags as it goes
     * @returns {boolean} true iff the record is valid
     */
    function fullValidationCheck() {
        $scope.preValidation();
        var record = $scope.getSelectedRecord();
        var recordValid = true;
        if (record && record.timeEntries) {
            angular.forEach(record.timeEntries, function (entry) {
                recordValid &= checkEntry(entry);
            })
        }
        return recordValid;
    }

    /**
     * Runs validation checks on the given entry
     * @param entry
     * @returns {boolean}
     */
    function checkEntry(entry) {
        var validationType = isSalariedEmployee(entry) ? 'raSa' : 'te';
        var entryValid = true;
        angular.forEach($scope.entryValidators[validationType], function (validate) {
            entryValid &= validate(entry);
        });
        return entryValid;
    }

    /**
     * This function is called before time entries are validated
     * this resets any error flags ( they will be restored if errors are detected during validation)
     * and also does any validations on the record scope
     */
    $scope.preValidation = function() {
        var record = $scope.getSelectedRecord();
        $scope.errorTypes.reset();
        checkForPrevUnsubmittedRaSa(record);
    };

    /**
     * Check for any unsubmitted salaried records before the given record
     * @param record
     */
    function checkForPrevUnsubmittedRaSa(record) {
        for (var iRecord in $scope.state.records) {
            var otherRecord = $scope.state.records[iRecord];
            if (moment(otherRecord.beginDate).isBefore(record.beginDate)) {
                for (var iEntry in otherRecord.timeEntries) {
                    if (isSalariedEmployee(otherRecord.timeEntries[iEntry])) {
                        $scope.errorTypes.record.prevUnsubmittedRecord = true;
                        return;
                    }
                }
            }
        }
    }

    /**
     * Search for and return any unsubmitted temporary time records before the given record.
     */
    function getPrevUnsubmittedTe() {
        var currentRec = $scope.getSelectedRecord();
        return $scope.state.records
            .filter(function (rec) {
                return rec.scope === 'E' &&
                    moment(rec.beginDate).isBefore(currentRec.beginDate);
            });
    }

    /**
     *  Error Types
     *  
     *  Categorized boolean flags that indicate if specific types of errors are present in a record
     */
    $scope.errorTypes = {
        // Error flags for regular / special annual pay time entries
        raSa: {
            workHoursInvalidRange: false,
            holidayHoursInvalidRange: false,
            vacationHoursInvalidRange: false,
            personalHoursInvalidRange: false,
            sickEmpHoursInvalidRange: false,
            sickFamHoursInvalidRange: false,
            miscHoursInvalidRange: false,
            totalHoursInvalidRange: false,
            notEnoughVacationTime: false,
            notEnoughPersonalTime: false,
            notEnoughSickTime: false,
            noMiscTypeGiven: false,
            noMiscHoursGiven: false,
            halfHourIncrements: false,
            notEnoughMiscTime: false
        },
        // Error messages for temporary pay time entries
        te: {
            workHoursInvalidRange: false,
            notEnoughWorkHours: false,
            noComment: false,
            noWorkHoursForComment: false,
            fifteenMinIncrements: false
        },
        // Record scope errors that do not depend on time entries
        record: {
            prevUnsubmittedRecord: false
        },
        // Recursively set all boolean error properties to false
        reset: function(object) {
            if (object === undefined) {
                object = this;
            }
            var caller = this;
            angular.forEach(object, function (value, key) {
                if (typeof value === 'boolean') {
                    object[key] = false;
                } else if (typeof value === 'object') {
                    caller.reset(value);
                }
            });
        }
    };

    /**
     *  --- Error Indication Methods ---
     *  These methods check the 'errorTypes' object for various types of errors
     */
    $scope.selRecordHasEntryErrors = function () {
        return $scope.selRecordHasRaSaErrors() || $scope.selRecordHasTeErrors();
    };

    $scope.selRecordHasRaSaErrors = function () {
        return !allFalse($scope.errorTypes.raSa);
    };

    $scope.selRecordHasTeErrors = function () {
        return !allFalse($scope.errorTypes.te);
    };

    $scope.selRecordHasRecordErrors = function () {
        return !allFalse($scope.errorTypes.record);
    };
    
    
    /** --- Validation Helper Methods --- */

    /**
     * A helper function that checks if entered sick time exceeds available sick time
     * If available sick time is exceeded, an error flag is set
     * @returns {boolean} indicating if available sick time is enough to cover entered sick time
     */
    function isEnoughSickTime() {
        var sickTotal = $scope.state.totals.sickEmpHours + $scope.state.totals.sickFamHours;
        if ($scope.state.accrual && sickTotal > $scope.state.accrual.sickAvailable) {
            $scope.errorTypes.raSa.notEnoughSickTime = true;
            return false;
        }
        return true;
    }

    /**
     * Checks that the given hours are divisible by 0.5 
     * according to the standard for regular / special annual time entry
     * @param hours
     * @returns {boolean}
     */
    // Todo Why are the hours modulated by 1 before 0.5/0.25 ?  
    // Seems harmless so I am leaving it in for now in case it is necessary for fp precision etc.
    function checkRaSaHourIncrements(hours) {
        if (isNaN(hours) || hours % 1 % 0.5 === 0) {
            return true;
        }
        $scope.errorTypes.raSa.halfHourIncrements = true;
        return false;
    }


    /**
     * Checks that the given hours are divisible by 0.25
     * according to the standard for temporary employee time entry
     * @param hours
     * @returns {boolean}
     */
    function checkTeHourIncrements(hours) {
        if (isNaN(hours) || hours % 1 % 0.25 === 0) {
            return true;
        }
        $scope.errorTypes.te.fifteenMinIncrements = true;
        return false;
    }

    /** --- Time Entry Validation Methods --- */
    
    $scope.entryValidators = {
        
        /** --- Regular / Special Annual time entry validators --- */
        
        raSa: {
            workHours: function (entry) {
                var hrs = entry.workHours;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                var isValid = true;
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.workHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            holidayHours: function (entry) {
                // Short circuit if entry is not special annual, holidays are not yet loaded, or the entry is a non holiday
                if (entry.payType !== 'SA' || !$scope.state.holidays || !$scope.isHoliday(entry)) {
                    return true;
                }
                var hrs = entry.holidayHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.holidayHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            vacationHours: function (entry) {
                var hrs = entry.vacationHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                if ($scope.state.accrual && $scope.state.totals.vacationHours > $scope.state.accrual.vacationAvailable) {
                    $scope.errorTypes.raSa.notEnoughVacationTime = true;
                    isValid = false;
                }
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.vacationHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            personalHours: function (entry) {
                var hrs = entry.personalHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                if ($scope.state.accrual && $scope.state.totals.personalHours > $scope.state.accrual.personalAvailable) {
                    $scope.errorTypes.raSa.notEnoughPersonalTime = true;
                    isValid = false;
                }
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.personalHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            sickEmpHours: function (entry) {
                var hrs = entry.sickEmpHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                isValid &= isEnoughSickTime();
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.sickEmpHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            sickFamHours: function (entry) {
                var hrs = entry.sickFamHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                isValid &= isEnoughSickTime();
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.sickFamHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);
                return isValid;
            },

            miscHours: function (entry) {
                var hrs = entry.miscHours;
                var isValid = true;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.raSa.miscHoursInvalidRange = true;
                    isValid = false;
                }
                isValid &= checkRaSaHourIncrements(hrs);

                var dateMoment = moment(entry.date);
                var entries = $scope.state.records[$scope.state.iSelectedRecord].timeEntries
                var grantInfoList = $scope.state.miscLeaveGrantInfoList

                for (var grantIndex in grantInfoList) {
                    var grantInfo = grantInfoList[grantIndex];
                    var tempGrantHours = grantInfo.hoursRemaining;
                    // Null signifies no limit to time used
                    if (tempGrantHours === null) {
                        continue;
                    }
                    var grant = grantInfo.grant;
                    for (var entryIndex in entries) {
                        var currEntry = entries[entryIndex];
                        if (!currEntry.miscHours) {
                            continue;
                        }
                        if (currEntry.miscHours && currEntry.miscType === grant.miscLeaveType
                            && !dateMoment.isBefore(grant.beginDate, 'day') && !dateMoment.isAfter(grant.endDate, 'day')) {
                            tempGrantHours = tempGrantHours - currEntry.miscHours;
                        }
                        if (tempGrantHours < 0) {
                            // We've gone over the hours, but it's not this entry's problem
                            if (currEntry !== entry) {
                                break;
                            }
                            $scope.errorTypes.raSa.notEnoughMiscTime = true;
                            $scope.state.miscLeaveUsageError =
                                {type: $scope.state.miscLeavesShortnameMap[entry.miscType], hours: grantInfo.hoursRemaining};
                            return false;
                        }
                    }
                }

                $scope.errorTypes.raSa.notEnoughMiscTime = false;
                $scope.state.miscLeaveUsageError = null;
                return isValid;
            },

            miscType: function (entry) {
                var miscTypePresent = entry.miscType !== null;
                var miscHoursPresent = entry.miscHours > 0;
                var isActiveRow = entry.index === activeRow.getActiveRow();
                if (!isActiveRow && !miscTypePresent && miscHoursPresent) {
                    $scope.errorTypes.raSa.noMiscTypeGiven = true;
                    return false;
                }
                if (miscTypePresent && !miscHoursPresent) {
                    $scope.errorTypes.raSa.noMiscHoursGiven = true;
                    return false;
                }
                return true;
            },

            totalHours: function (entry) {
                // Don't invalidate total entry if its not a number.
                // This is to avoid confusion since no number is displayed to the user.
                if (isNaN(entry.total) || entry.total >= 0 && entry.total <= 24) {
                    return true;
                }
                $scope.errorTypes.raSa.totalHoursInvalidRange = true;
                return false;
            }
        },
        
        /** --- Temporary Time Entry Validators --- */
        
        te: {
            workHours: function (entry) {
                var hrs = entry.workHours;
                if (hrs === 0 || hrs === null) { // Short circuit to true if hours are null or 0
                    return true;
                }
                var isValid = true;
                if (typeof hrs === 'undefined') {
                    $scope.errorTypes.te.workHoursInvalidRange = true;
                    isValid = false;
                }
                if ($scope.getAvailableHours() < 0) {
                    $scope.errorTypes.te.notEnoughWorkHours = true;
                    isValid = false;
                }
                isValid &= checkTeHourIncrements(hrs);
                return isValid;
            },
            comment: function (entry) {
                var hrs = entry.workHours;
                var comment = entry.empComment;
                var isActiveRow = entry.index === activeRow.getActiveRow();
                if (hrs > 0 && !comment && !isActiveRow) {
                    $scope.errorTypes.te.noComment = true;
                    return false;
                }
                if (hrs === null && comment) {
                    $scope.errorTypes.te.noWorkHoursForComment = true;
                    return false;
                }
                return true;
            }
        }
    };

    /** --- Initialization --- */

    $scope.init();
}