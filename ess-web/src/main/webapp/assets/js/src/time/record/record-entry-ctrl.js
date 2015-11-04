var essApp = angular.module('ess')
        .controller('RecordEntryController', ['$scope', '$filter', '$q', '$timeout', 'appProps',
                                              'ActiveTimeRecordsApi', 'TimeRecordsApi', 'AccrualPeriodApi', 'AllowanceApi',
                                              'RecordUtils', 'LocationService', 'modals', recordEntryCtrl]);

function recordEntryCtrl($scope, $filter, $q, $timeout, appProps, activeRecordsApi,
                         recordsApi, accrualPeriodApi, allowanceApi, recordUtils, locationService, modals) {

    function getInitialState() {
        return {
            empId: appProps.user.employeeId,  // Employee Id
            miscLeaves: appProps.miscLeaves,  // Listing of misc leave types
            accrual: null,                    // Accrual info for selected record
            allowances: {},                   // A map that stores yearly temp employee allowances
            selectedYear: 0,                  // The year of the selected record (makes it easy to get the selected record's allowance)
            records: [],                      // All active employee records
            iSelectedRecord: 0,               // Index of the currently selected record,
            salaryRecs: [],                   // A list of salary recs that are active during the selected record's date range
            iSelSalRec: 0,                    // Index of the selected salary rec (used when there is a salary change mid record)
            tempEntries: false,               // True if the selected record contains TE pay entries
            annualEntries: false,             // True if the selected record contains RA or SA entries
            totals: {},                       // Stores record wide totals for time entry fields of the selected record

            // Page state
            pageState: 0                      // References the values from $scope.pageStates
        }
    }

    $scope.state = null;                  // The container for all the state variables for this page

    // Enumeration of the possible page states.
    $scope.pageStates = {
        INITIAL: 0,
        FETCHING: 1,
        FETCHED: 2,
        SAVING: 3,
        SAVED: 4,
        SAVE_FAILURE: 5,
        SUBMIT_ACK: 6,
        SUBMITTING: 7,
        SUBMITTED: 8,
        SUBMIT_FAILURE: 9
    };

    // Create a new state from the values in the default state.
    $scope.initializeState = function() {
        $scope.state = getInitialState();
    };

    // A map of employee record statuses to the logical next status upon record submission
    var nextStatusMap = {
        NOT_SUBMITTED: "SUBMITTED",
        DISAPPROVED: "SUBMITTED",
        DISAPPROVED_PERSONNEL: "SUBMITTED_PERSONNEL"
    };

    $scope.init = function() {
        console.log('Time record initialization');
        $scope.initializeState();
        $scope.getRecords();
    };

    /** --- Watches --- */

    // Update accruals, display entries, totals when a new record is selected
    $scope.$watchGroup(['state.records', 'state.iSelectedRecord'], function() {
        if ($scope.state.records && $scope.state.records[$scope.state.iSelectedRecord]) {
            detectPayTypes();
            $q.all([    // Get Accruals/Allowances for the record and then call record update methods
                $scope.getAccrualForSelectedRecord(),
                $scope.getAllowanceForSelRecord()
            ]).then(function() {
                console.log('allowances/accruals got, calling update functions');
                getSelectedSalaryRecs();
                onRecordChange();
                setRecordSearchParams();
            });
        }
    });

    /** --- API Methods --- */

    /**
     * Fetches the employee's active records from the server, auto-selecting a record
     * if it's end date is supplied in the query params.
     */
    $scope.getRecords = function() {
        $scope.initializeState();
        $scope.state.pageState = $scope.pageStates.FETCHING;
        activeRecordsApi.get({
            empId: $scope.state.empId,
            scope: 'E'
        }, function (response) {
            if ($scope.state.empId in response.result.items) {
                $scope.state.records = response.result.items[$scope.state.empId];
                angular.forEach($scope.state.records, function(record, index) {
                    // Compute the due from dates for each record
                    var endDateMoment = moment(record.endDate);
                    record.dueFromNowStr = endDateMoment.fromNow(false);
                    record.isDue = endDateMoment.isBefore(moment().add(1, 'days').startOf('day'));
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
        }, function (response) {    // handle request error
            modals.open('500', {action: 'get active records', details: response});
        }).$promise.finally(function() {
            $scope.state.pageState = $scope.pageStates.FETCHED;
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
        // TODO: Validate the current record
        // Open the modal to indicate save/submit
        if (submit) {
            $scope.state.pageState = $scope.pageStates.SUBMIT_ACK;
            modals.open('submit-indicator', {'record': record});
        }
        else {
            modals.open('save-indicator', {'record': record});
            $scope.state.pageState = $scope.pageStates.SAVING;
            var currentStatus = record.recordStatus;
            record.recordStatus = 'NOT_SUBMITTED';
            recordsApi.save(record, function (resp) {
                record.updateDate = moment().format('YYYY-MM-DDTHH:mm:ss.SSS');
                record.savedDate = record.updateDate;
                record.dirty = false;
                $scope.state.pageState = $scope.pageStates.SAVED;
            }, function (resp) {
                if (resp.status === 400) {
                    // todo invalid record response
                } else {
                    modals.open('500', {details: resp});
                    console.log(resp);
                }
                $scope.state.pageState = $scope.pageStates.SAVE_FAILURE;
                record.recordStatus = currentStatus;
            });
        }
    };

    /**
     * Submits the currently selected record. This assumes any necessary validation has already been
     * made on this record.
     */
    $scope.submitRecord = function() {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        var currentStatus = record.recordStatus;
        record.recordStatus = 'SUBMITTED';
        $scope.state.pageState = $scope.pageStates.SUBMITTING;
        recordsApi.save(record, function (resp) {
            $scope.state.pageState = $scope.pageStates.SUBMITTED;
        }, function (resp) {
            if (resp.status === 400) {
                //todo invalid record response
            } else {
                modals.open('500', {details: resp});
                console.log(resp);
            }
            $scope.state.pageState = $scope.pageStates.SUBMIT_FAILURE;
            record.recordStatus = currentStatus;
        });
    };

    /**
     * Fetches the accruals for the currently selected time record from the server.
     * @returns Promise that is fulfilled when the accruals are received
     */
    $scope.getAccrualForSelectedRecord = function() {
        if ($scope.state.annualEntries) {
            var empId = $scope.state.empId;
            var record = $scope.state.records[$scope.state.iSelectedRecord];
            var periodStartMoment = moment(record.payPeriod.startDate);
            return accrualPeriodApi.get({
                empId: empId,
                beforeDate: periodStartMoment.format('YYYY-MM-DD')
            }, function (resp) {
                if (resp.success) {
                    $scope.state.accrual = resp.result;
                }
            }, function (resp) {
                modals.open('500', {details: resp});
                console.log(resp);
            }).$promise;
        }
        // Return an automatically resolving promise if no request was made
        return $q(function (resolve) {resolve()});
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
            return allowanceApi.get(params, function(response) {
                for (var i in response.result) {
                    var allowance = response.result[i];
                    console.log('got allowance', allowance.empId, allowance.year);
                    $scope.state.allowances[allowance.year] = allowance;
                }
            }, function(resp) {
                modals.open('500', {details: resp});
                console.log(resp);
            }).$promise;
        }
        // Return an automatically resolving promise if no request was made
        return $q(function (resolve) {resolve()});
    };

    /** --- Display Methods --- */

    /**
     * Returns the currently selected record.
     * @returns timeRecord object
     */
    $scope.getSelectedRecord = function() {
        return $scope.state.records[$scope.state.iSelectedRecord];
    };

    $scope.finishSubmitModal = function() {
        $scope.closeModal();
        $scope.init();
    };

    /**
     * Closes any open modals by resolving them.
     */
    $scope.closeModal = function() {
        modals.resolve();
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
    $scope.setDirty = function() {
        $scope.state.records[$scope.state.iSelectedRecord].dirty = true;
        onRecordChange();
    };

    /**
     * Returns true if the record is submittable, i.e. it exists, passes all validations, and has ended or will end
     * today.
     * @returns {boolean}
     */
    $scope.recordSubmittable = function () {
        var record = $scope.state.records[$scope.state.iSelectedRecord];
        return record && !$scope.errorTypes.raSa.errors && !$scope.errorTypes.te.errors && !moment(record.endDate).isAfter(moment(), 'day');
    };

    /**
     * Get the number of available work hours at the selected salary rate
     *  such that the record cost does not exceed the employee's annual allowance
     * @returns {number}
     */
    $scope.getAvailableHours = function() {
        var record = $scope.getSelectedRecord();
        var allowance = $scope.state.allowances[$scope.state.selectedYear];
        var salaryRec = $scope.state.salaryRecs[$scope.state.iSelSalRec];

        if (!record || !allowance || !salaryRec) {
            return 0;
        }

        var availableMoney = allowance.yearlyAllowance - allowance.moneyUsed - record.moneyUsed;
        var availableHours = availableMoney / salaryRec.salaryRate;
        // Adjust hours into a multiple of 0.25
        return Math.round((availableHours - Math.abs(availableHours) % 0.25) * 4) / 4;
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
        calculateAllowanceUsage();
        checkRecordForErrors(record);
    }

    /**
     * Ensure that all time entered is in multiples of 0.25 or 0.5 for Temporary and Annual entries respectively
     * @param record
     */
    function sanitizeEntries(record) {
        var timeEntryFields = recordUtils.getTimeEntryFields();
        angular.forEach(record.timeEntries, function (entry) {
            var validInterval = isTemporaryEmployee(entry) ? 0.25 : 0.5;
            var inverse = 1/validInterval;
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
        angular.forEach(allowance.salaryRecs, function (salaryRec) {
            // Select only temporary salaries that are effective during the record date range
            if (salaryRec.payType === 'TE' &&
                    !moment(salaryRec.effectDate).isAfter(record.endDate) &&
                    !moment(record.beginDate).isAfter(salaryRec.endDate)) {
                salaryRecs.push(salaryRec);
            }
        });
    }

    /**
     * Computes the payout cost of the selected time record
     */
    function calculateAllowanceUsage() {
        if ($scope.state.records.length > 0 && $scope.state.tempEntries) {
            var record = $scope.getSelectedRecord();
            record.moneyUsed = 0;
            for (var i in record.timeEntries) {
                var entry = record.timeEntries[i];
                if (isTemporaryEmployee(entry) && entry.workHours) {
                    record.moneyUsed += entry.workHours * getSalaryAtDate(entry.date);
                }
            }
        }
    }

    /**
     * Gets the employee's hourly salary at the given date
     * @param date
     * @returns {Number}
     */
    function getSalaryAtDate(date) {
        var momentDate = moment(date);
        if (momentDate.isValid() && $scope.state.allowances.hasOwnProperty(momentDate.year())) {
            var year = momentDate.year();
            var allowance = $scope.state.allowances[year];
            for (var i in allowance.salaryRecs) {
                var salaryRec = allowance.salaryRecs[i];
                if (!momentDate.isAfter(salaryRec.endDate) && !momentDate.isBefore(salaryRec.effectDate)) {
                    return salaryRec.salaryRate;
                }
            }
        }
        return "no salary for date...";
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
                    $scope.state.iSelectedRecord = iRecord;
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

    /** --- Validation --- **/

    /** Check for errors in the record, any errors are reflected in the $scope.errorTypes object. */
    function checkRecordForErrors(record) {
        if (record && record.timeEntries) {
            $scope.errorTypes.reset();
            angular.forEach(record.timeEntries, function (entry) {
                if (isSalariedEmployee(entry)) {
                    checkSalariedEntryForErrors(entry);
                }
                else if (isTemporaryEmployee(entry)) {
                    checkTempEntryForErrors(entry);
                }
            });
            // These will be true if any errors of their type exist. raSa for salaried, te for temporary.
            $scope.errorTypes.raSa.errors = !allFalse($scope.errorTypes.raSa);
            $scope.errorTypes.te.errors = !allFalse($scope.errorTypes.te);
        }
    }

    /** Checks all input in a salaried employees time entry record for errors. */
    function checkSalariedEntryForErrors(entry) {
        if (typeof entry.workHours === 'undefined') {
            $scope.errorTypes.raSa.workHoursInvalidRange = true;
        }
        if (typeof entry.vacationHours === 'undefined') {
            $scope.errorTypes.raSa.vacationHoursInvalidRange = true;
        }
        if (typeof entry.personalHours === 'undefined') {
            $scope.errorTypes.raSa.personalHoursInvalidRange = true;
        }
        if (typeof entry.sickEmpHours === 'undefined') {
            $scope.errorTypes.raSa.empSickHoursInvalidRange = true;
        }
        if (typeof entry.sickFamHours === 'undefined') {
            $scope.errorTypes.raSa.famSickHoursInvalidRange = true;
        }
        if (typeof entry.miscHours === 'undefined') {
            $scope.errorTypes.raSa.miscHoursInvalidRange = true;
        }
        if (entry.total < 0 || entry.total > 24) {
            $scope.errorTypes.raSa.totalHoursInvalidRange = true;
        }
        if ($scope.state.totals.vacationHours > $scope.state.accrual.vacationAvailable) {
            $scope.errorTypes.raSa.notEnoughVacationTime = true;
        }
        if ($scope.state.totals.personalHours > $scope.state.accrual.personalAvailable) {
            $scope.errorTypes.raSa.notEnoughPersonalTime = true;
        }
        if ($scope.state.totals.sickEmpHours + $scope.state.totals.sickFamHours > $scope.state.accrual.sickAvailable) {
            $scope.errorTypes.raSa.notEnoughSickTime = true;
        }
        // TODO: delay this some so they have a chance to enter a misc type.
        if (typeof entry.miscHours !== 'undefined' && entry.miscHours !== null && entry.miscType === null) {
            $scope.errorTypes.raSa.noMiscTypeGiven = true;
        }
        if (!allEntryValuesInHalfHourIncrements(entry)) {
            $scope.errorTypes.raSa.halfHourIncrements = true;
        }
    }

    /** Checks all input in a temporary employees time entry record for errors. */
    function checkTempEntryForErrors(entry) {
        if (typeof entry.workHours === 'undefined') {
            $scope.errorTypes.te.workHoursInvalidRange = true;
        }
        if (entry.workHours > 0 && $scope.getAvailableHours() < 0) {
            $scope.errorTypes.te.notEnoughWorkHours = true;
        }
        if (!isInFifteenMinIncrements(entry.workHours)) {
            $scope.errorTypes.te.fifteenMinIncrements = true;
        }
    }

    $scope.errorTypes = {
        // Error messages to display in RA SA time entry form.
        raSa: {
            errors: false,
            workHoursInvalidRange: false,
            vacationHoursInvalidRange: false,
            personalHoursInvalidRange: false,
            empSickHoursInvalidRange: false,
            famSickHoursInvalidRange: false,
            miscHoursInvalidRange: false,
            totalHoursInvalidRange: false,
            notEnoughVacationTime: false,
            notEnoughPersonalTime: false,
            notEnoughSickTime: false,
            noMiscTypeGiven: false,
            halfHourIncrements: false
        },
        // Error messages to display in TE time entry form.
        te: {
            errors: false,
            workHoursInvalidRange: false,
            notEnoughWorkHours: false,
            fifteenMinIncrements: false
        },
        reset: function() {
            this.raSa.errors = false;
            this.raSa.workHoursInvalidRange = false;
            this.raSa.vacationHoursInvalidRange = false;
            this.raSa.personalHoursInvalidRange = false;
            this.raSa.empSickHoursInvalidRange = false;
            this.raSa.famSickHoursInvalidRange = false;
            this.raSa.miscHoursInvalidRange = false;
            this.raSa.totalHoursInvalidRange = false;
            this.raSa.notEnoughVacationTime = false;
            this.raSa.notEnoughPersonalTime = false;
            this.raSa.notEnoughSickTime = false;
            this.raSa.noMiscTypeGiven = false;
            this.raSa.halfHourIncrements = false;
            this.te.errors = false;
            this.te.workHoursInvalidRange = false;
            this.te.notEnoughWorkHours = false;
            this.te.fifteenMinIncrements = false;
        }
    };

    $scope.areWorkHoursValid = function(entry) {
        var hrs = entry.workHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        if (isTemporaryEmployee(entry)) {
            return isInFifteenMinIncrements(hrs);
        }
        else if (isSalariedEmployee(entry)) {
            return isInHalfHourIncrements(hrs);
        }
        return true;
    };

    $scope.areVacationHoursValid = function(entry) {
        var hrs = entry.vacationHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        var hasRequestedHours = $scope.state.accrual && $scope.state.totals.vacationHours <= $scope.state.accrual.vacationAvailable;
        return hasRequestedHours && isInHalfHourIncrements(hrs);
    };

    $scope.arePersonalHoursValid = function(entry) {
        var hrs = entry.personalHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        var hasRequestedHours = $scope.state.accrual && $scope.state.totals.personalHours <= $scope.state.accrual.personalAvailable;
        return hasRequestedHours && isInHalfHourIncrements(hrs);
    };

    $scope.areEmpSickHoursValid = function(entry) {
        var hrs = entry.sickEmpHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        var hasRequestedHours = $scope.state.accrual && $scope.state.totals.sickEmpHours + $scope.state.totals.sickFamHours <= $scope.state.accrual.sickAvailable;
        return hasRequestedHours && isInHalfHourIncrements(hrs);
    };

    $scope.areFamSickHoursValid = function(entry) {
        var hrs = entry.sickFamHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        var hasRequestedHours = $scope.state.accrual && $scope.state.totals.sickEmpHours + $scope.state.totals.sickFamHours <= $scope.state.accrual.sickAvailable;
        return hasRequestedHours && isInHalfHourIncrements(hrs);
    };

    $scope.areMiscHoursValid = function(entry) {
        var hrs = entry.miscHours;
        if (typeof hrs === 'undefined') {
            return false;
        }
        if (hrs === null) {
            return true;
        }
        return isInHalfHourIncrements(hrs);
    };

    $scope.areTotalHoursValid = function(entry) {
        // Don't invalidate total entry if its not a number.
        // This is to avoid confusion since no number is displayed to the user.
        if (isNaN(entry.total)) {
            return true;
        }
        return entry.total >= 0 && entry.total <= 24;
    };

    $scope.isMiscTypeMissing = function(entry) {
        return entry.miscHours > 0 && entry.miscType === null;
    };

    function allEntryValuesInHalfHourIncrements(entry) {
        return isInHalfHourIncrements(entry.workHours) && isInHalfHourIncrements(entry.vacationHours) && isInHalfHourIncrements(entry.personalHours)
            && isInHalfHourIncrements(entry.sickEmpHours) && isInHalfHourIncrements(entry.sickFamHours) && isInHalfHourIncrements(entry.miscHours);
    }

    function isInHalfHourIncrements(hours) {
        // Don't check increment if undefined.
        if (typeof hours === 'undefined') {
            return true;
        }
        return hours % 1 % 0.5 === 0;
    }

    function isInFifteenMinIncrements(hours) {
        // Don't check increment if undefined.
        if (typeof hours === 'undefined') {
            return true;
        }
        return hours % 1 % 0.25 === 0;
    }

    /** --- Initialization --- */

    $scope.init();
}