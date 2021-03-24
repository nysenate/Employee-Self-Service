var essApp = angular.module('ess');

essApp.controller('RecordManageCtrl', ['$scope', '$q', '$filter',
                                       'appProps', 'RecordUtils', 'modals', 'badgeService', 'supEmpGroupService',
                                       'SupervisorTimeRecordsApi', 'TimeRecordReviewApi', 'TimeRecordReminderApi',
    recordManageCtrl]);

function recordManageCtrl($scope, $q, $filter,
                          appProps, recordUtils, modals, badgeService, supEmpGroupService,
                          supRecordsApi, timeRecordReviewApi, reminderApi) {

    /** Object used as default template for selected indices */
    var initialSelectedIndices = {
        NOT_SUBMITTED: {},
        SUBMITTED: {},
        DISAPPROVED: {}
    };

    $scope.state = {
        // Data
        /** List of supervisor entries containing sup info and records */
        supervisors: [],
        /** Index of currently selected supervisor */
        iSelSup: -1,
        /** Emp Id of the logged in user */
        userEmpId: appProps.user.employeeId,

        // Page state
        /** Flags set as true if the named request is in progress */
        request: {
            supEmpGroup: false,
            records: false,
            recordSubmit: false,
            emailReminder: false
        },

        inactiveEmps: [],

        /** Mapping of selected record indices by status code, e,g {'SUBMITTED' : { 1:true, 2:true }} */
        selectedIndices: angular.copy(initialSelectedIndices)
    };

    /* --- Initialization --- */

    /**
     * Start retrieval of supervisor employee groups,
     * Calling the supervisor initialization method on completion
     */
    $scope.state.request.supEmpGroup = true;
    supEmpGroupService.init()
        .then(initializeSupervisors)
        .finally(function () {
            $scope.state.request.supEmpGroup = false;
        });

    /* --- Watches --- */

    /** Refresh records when the selected supervisor changes */
    $scope.$watch('state.iSelSup', getActiveRecordsForSelSup);

    /* --- Api Methods --- */

    /**
     * Retrieves all active, in progress records that are under supervision of the selected supervisor
     * Stores records in a map by supervisor id
     * Records are not requested if the supervisor's records have already been retrieved
     */
    function getActiveRecordsForSelSup() {

        var supEntry = $scope.getSelSupEntry();

        if (!supEntry) {
            return;
        }

        getEmployeeActiveRecords(supEntry)
    }

    /**
     * Retrieve employee records for the given supervisor entry
     *
     * @param supEntry
     * @returns {*|Observable}
     */
    function getEmployeeActiveRecords(supEntry) {
        // Get the supId used to query records for this entry
        var querySupId = supEntry.querySupId;

        var fromMoment = moment().subtract(1, 'year');
        var toMoment = moment().add(1, 'month');

        // Trim query dates according to effective dates of supervision, if necessary
        if (supEntry.extendedSup) {
            fromMoment = moment.max(fromMoment, moment(supEntry.effectiveFrom));
            toMoment = moment.min(toMoment, moment(supEntry.effectiveTo));
        }

        var params = {
            from: fromMoment.format('YYYY-MM-DD'),
            to: toMoment.format('YYYY-MM-DD'),
            supId: querySupId
        };
        $scope.state.request.records = true;
        return supRecordsApi.get(params, onSuccess, $scope.handleErrorResponse)
            .$promise.finally(function() {$scope.state.request.records = false;});
        function onSuccess(resp) {
            initializeRecords(querySupId, resp.result.items);
            resetSelection();
        }
    }

    /**
     * Submits each record of an array of records
     * Refreshes record data after each record has been submitted
     */
    function submitRecords (records) {
        var promises = [];
        records.forEach(function(record) {
            var params = {
                timeRecordId: record.timeRecordId,
                action: record.action,
                remarks: record.remarks
            };
            promises.push(timeRecordReviewApi.save(params, {},
                function(response){console.log('success:', record.timeRecordId, response)},
                function(response){console.log('fail:', record.timeRecordId, response); return response;}
            ).$promise);
        });
        $scope.state.request.recordSubmit = true;
        return $q.all(promises)
            .then(function onFulfilled() {
                console.log(records.length, 'records submitted');
                // Refresh records for the logged in supervisor
                getEmployeeActiveRecords($scope.state.supervisors[0])
                    .then(function () {
                        // If the selected supervisor was an indirect supervisor, refresh their records too
                        if ($scope.getSelSupEntry().querySupId !== appProps.user.employeeId) {
                            getEmployeeActiveRecords($scope.getSelSupEntry());
                        }
                    });
            })
            .catch($scope.handleErrorResponse)
            .finally(function () {
                $scope.state.request.recordSubmit = false;
            })
    }

    /**
     * Posts request to send email reminders for the given time records
     * @param timeRecords
     * @returns {*} promise resolving on a successful request
     */
    function postReminders(timeRecords) {
        if (!timeRecords) {
            return $q.when();
        }
        modals.open('record-reminder-posting');
        return reminderApi.save(null, timeRecords,
            function onSuccess(reminders) {
                console.log('time record reminders sent.');
                console.log(reminders.result);
                // Close 'record-reminder-posting' modal
                modals.rejectAll();
                modals.open('record-reminder-posted', {reminders: reminders.result}, true)
                    .then(function() {
                        modals.rejectAll();
                    })
            }, $scope.handleErrorResponse
            ).$promise
    }

    /* --- Display Methods --- */

    /**
     * Convenience method that returns the selected supervisor
     */
    $scope.getSelSupEntry = function () {
        if ($scope.state.iSelSup < 0) {
            return null;
        }
        return $scope.state.supervisors[$scope.state.iSelSup];
    };

    /**
     * Get a list of record with the given status for the selected supervisor
     * @param status
     */
    $scope.getRecords = function (status) {
        var supEntry = $scope.getSelSupEntry();
        if (!supEntry) {
            return null;
        }

        return getRecords(supEntry, status);
    };

    /**
     * Return's true iff the selected supervisor has records of the given status
     * @param status
     * @returns {boolean}
     */
    $scope.hasRecords = function (status) {
        return ($scope.getRecords(status) || []).length > 0;
    };

    /**
     * Causes all SUBMITTED records to be selected for review
     */
    $scope.selectAll = function(status) {
        var records = $scope.getRecords(status);
        for(var i = 0; i < records.length; i++) {
            // Don't select the user's own submitted records.
            if (status === 'SUBMITTED' && records[i].employeeId === $scope.state.userEmpId) {
                continue;
            }
            $scope.state.selectedIndices[status][i] = true;
        }
    };
    /**
     * Clears all SUBMITTED currently selected for review
     */
    $scope.selectNone = function(status) {
        $scope.state.selectedIndices[status] = {};
    };

    /**
     * Open a record detail modal using the given record as data
     * @param record
     */
    $scope.showDetails = function(record) {
        var params = { record: record };
        modals.open('record-details', params, true);
    };

    /**
     * Opens a record review modal in which the supervisor will view record details and approve/reject records accordingly
     * Passes all selected records
     * Upon resolution, the modal will return an object containing
     */
    $scope.review = function(status, allowApproval) {
        var omitOwnRecords = allowApproval;
        var selectedRecords = getSelectedRecords(status, omitOwnRecords);
        var params = {
            records: selectedRecords,
            allowApproval: allowApproval
        };
        modals.open('record-review', params, !allowApproval)
            .then(submitReviewedRecords);
    };

    /**
     * Return true iff there are any selected records of the given status
     * @param status
     * @returns {boolean}
     */
    $scope.hasSelections = function(status) {
        for (var p in $scope.state.selectedIndices[status]) {
            if ($scope.state.selectedIndices[status].hasOwnProperty(p) && $scope.state.selectedIndices[status][p] === true) {
                return true;
            }
        }
        return false;
    };

    /**
     * Submits all displayed records that are awaiting supervisor approval as 'APPROVED'
     * after the user confirms via a prompt
     */
    $scope.approveSelections = function () {
        var selectedRecords = getSelectedRecords('SUBMITTED', true);
        if (selectedRecords) {
            selectedRecords.forEach(function (record) {
                record.action = "submit";
            });
            var params = {approved: selectedRecords};
            return modals.open('record-approval-submit', params)
                .then(function() {
                    console.log("meow");
                    submitRecords(selectedRecords);
                });
        }
    };

    /**
     * Sends email reminders for all selected records of the given status
     * @param status
     */
    $scope.remindSelections = function (status) {
        var selectedRecords = getSelectedRecords(status);
        modals.open('record-reminder-prompt', {records: selectedRecords}, true)
            .then(function () {
                return postReminders(selectedRecords);
            });
    };

    /**
     * Exposes modal resolve method
     * @see modals.resolve
     * @type {resolve}
     */
    $scope.resolveModal = modals.resolve;

    /** --- Internal Methods --- */

    /**
     * Generate supervisor entries for the user, their overrides, and any employees that are supervisors
     * Reset the selected supervisor
     */
    function initializeSupervisors () {
        $scope.state.supervisors = getPrimarySupEntries()
            .concat(getOverrideSupEntries())
            .concat(getEmpOverrideEntries())
            .concat(getExtendedSupEntries());
        $scope.state.iSelSup = 0;
    }

    /**
     * Return up to two supervisor entries for the logged in user
     *  - One for the user and all overrides (if overrides are in effect)
     *  - One for only the user's primary employees
     * @returns {Array}
     */
    function getPrimarySupEntries () {
        var primarySupEntries = [];
        var supId = appProps.user.employeeId;

        var extSupEmpGroup = supEmpGroupService.getExtSupEmpGroup();
        var supName = supEmpGroupService.getName(supId);

        var hasSupOverrides = extSupEmpGroup.supOverrideEmployees.size > 0;
        var hasEmpOverrides = extSupEmpGroup.empOverrideEmployees.length > 0;

        // Create an entry for all of the supervisor's records
        var fullEmpGroupEntry = {
            querySupId: supId,
            supId: supId,
            name: supName,
            baseLabel: supName.fullName,
            dropDownLabel: supName.fullName,
            fullEmpGroup: true,
            userResponsible: true
        };

        primarySupEntries.push(fullEmpGroupEntry);

        // If there are overrides, modify the full emp group entry
        // and add an entry for the supervisor without overrides
        if (hasSupOverrides || hasEmpOverrides) {
            // Add the entry for just the supervisor
            var supEmpGroupEntry = angular.copy(fullEmpGroupEntry);
            supEmpGroupEntry.fullEmpGroup = false;
            primarySupEntries.push(supEmpGroupEntry);

            // Modify full entry to indicate that it includes overrides
            var fullEmpGroupBaseLabel = supName.fullName + ' + Overrides';
            fullEmpGroupEntry.baseLabel = fullEmpGroupBaseLabel;
            fullEmpGroupEntry.dropDownLabel = fullEmpGroupBaseLabel;
        }

        return primarySupEntries;
    }

    /**
     * Generate and return supervisor entries for any supervisor overrides the user may have
     * @returns {Array}
     */
    function getOverrideSupEntries() {
        var extSupEmpGroup = supEmpGroupService.getExtSupEmpGroup();

        // Store sup overrides in a map to group by supId
        var supOverrides = {};
        var supOverrideGroup = 'Supervisor Overrides';
        angular.forEach(extSupEmpGroup.supOverrideEmployees.items, function (empInfos, supId) {
            supId = parseInt(supId);
            var name = supEmpGroupService.getName(supId);
            var baseLabel = getSupNameLabel(name);
            supOverrides[supId] = {
                querySupId: appProps.user.employeeId,
                supId: supId,
                name: name,
                group: supOverrideGroup,
                baseLabel: baseLabel,
                dropDownLabel: baseLabel,
                supOverride: true,
                userResponsible: true
            };
        });

        var supOvrList = Object.keys(supOverrides)
            .map(function (k) {
                return supOverrides[k]
            });

        supOvrList = $filter('orderBy')(supOvrList, 'dropDownLabel');

        return supOvrList;
    }

    /**
     * Generate and return supervisor entries for any employee overrides the user may have
     * @returns {Array}
     */
    function getEmpOverrideEntries() {
        var extSupEmpGroup = supEmpGroupService.getExtSupEmpGroup();

        // Store sup overrides in a map to group by supId
        var empOverrides = {};
        var empOverrideGroup = 'Employee Overrides';
        angular.forEach(extSupEmpGroup.empOverrideEmployees, function (empInfo) {
            var name = supEmpGroupService.getName(empInfo.empId),
                baseLabel = getSupNameLabel(name);
            empOverrides[empInfo.empId] = {
                querySupId: appProps.user.employeeId,
                supId: empInfo.supId,
                ovrEmpId: empInfo.empId,
                name: name,
                group: empOverrideGroup,
                baseLabel: baseLabel,
                dropDownLabel: baseLabel,
                empOverride: true,
                userResponsible: true
            }
        });

        var empOvrList = Object.keys(empOverrides)
            .map(function (k) { return empOverrides[k] });

        empOvrList = $filter('orderBy')(empOvrList, 'dropDownLabel');

        return empOvrList;
    }

    /**
     * Generate and return supervisor entries for each of the user's employees that is also a supervisor
     * @returns {Array}
     */
    function getExtendedSupEntries() {
        var supEmpGroups = supEmpGroupService.getSupEmpGroupList();

        var extSupEntries =  supEmpGroups.slice(1)
            .filter(function (empGroup) {
                // Filter out emp groups that ended over a year ago
                return moment().subtract(1, 'year')
                    .isBefore(empGroup.effectiveTo);
            })
            .map(function (empGroup) {
                var supId = empGroup.supId,
                    name = supEmpGroupService.getName(supId),
                    baseLabel = getSupNameLabel(name),
                    supSupId = empGroup.supSupId,
                    supSupName = supEmpGroupService.getName(supSupId),
                    extSupGroup = 'Supervisors Under ' + supSupName.fullName;
                return {
                    querySupId: supId,
                    supId: supId,
                    name: name,
                    group: extSupGroup,
                    baseLabel: baseLabel,
                    dropDownLabel: baseLabel,
                    extendedSup: true,
                    effectiveFrom: empGroup.effectiveFrom,
                    effectiveTo: empGroup.effectiveTo
                }
            });

        return extSupEntries;
    }

    /**
     * Return a display name from the given name object
     * @param name
     * @returns {string}
     */
    function getSupNameLabel(name) {
        return name.lastName + ', ' + name.firstName;
    }

    /**
     * Calculates totals for each given record
     * Stores records into appropriate supEntry objects keyed by record status
     */
    function initializeRecords(supId, recordMap) {
        var isUser = supId === appProps.user.employeeId;

        var affectedSupervisors = $scope.state.supervisors.filter(function (supEntry) {
            return supEntry.querySupId === supId;
        });

        // Clear existing records for affected supervisors
        affectedSupervisors.forEach(function (supEntry) {
            supEntry.records = {};
        });

        // Store records in record map
        angular.forEach(recordMap, function(records, empId) {
            angular.forEach(records, function(record) {
                // Ignore the record if it is an override for an indirect employee
                if (!isUser && record.supervisorId !== supId) {
                    return;
                }
                // Ignore the record if it is a blank, employee scoped, temp employee record
                if (record.scope === 'E' &&
                    recordUtils.isFullTempRecord(record) &&
                    !recordUtils.recordHasEnteredTime(record))
                {
                    return;
                }
                // Compute totals for the record
                recordUtils.calculateDailyTotals(record);
                record.totals = recordUtils.getRecordTotals(record);
                affectedSupervisors
                    .filter(function (supEntry) {   // Only add the record to the full emp group and the relevant sup entry
                        return supEntry.fullEmpGroup ||
                            supEntry.supId === record.supervisorId &&
                            (!supEntry.ovrEmpId || supEntry.ovrEmpId === record.employeeId);
                    })
                    .forEach(function (supEntry) {
                        var status = record.recordStatus;
                        // Get or create status list
                        var statusList = supEntry.records[status] = supEntry.records[status] || [];
                        statusList.push(record);
                    });
            });
        });

        // Sort stored records
        affectedSupervisors.forEach(function (supEntry) {
            angular.forEach(supEntry.records, function (recordList, index) {
                supEntry.records[index] =
                    $filter('orderBy')(recordList, ['employee.lastName', 'employee.firstName', 'beginDate']);
            })
        });

        if (isUser) {
            updatePendingRecordCounts();
        }
    }

    /**
     * Set the pending records text in the dropdown labels
     * Also updates pending record badge
     */
    function updatePendingRecordCounts() {
        angular.forEach($scope.state.supervisors, function (supEntry) {
            if (supEntry.extendedSup) {
                return;
            }

            var count = getRecords(supEntry, 'SUBMITTED').length;

            if (supEntry.fullEmpGroup) {
                badgeService.setBadgeValue('pendingRecordCount', count);
            }

            var pendingText =  ' - (' + count + ' Pending Records)';
            supEntry.dropDownLabel = supEntry.baseLabel + pendingText;
        });
    }

    /**
     * Gets a list of records for the given supervisor, with the given status
     * Will filter by an override supervisor if an id is provided
     * @param supEntry
     * @param status
     * @returns {*|Array}
     */
    function getRecords(supEntry, status) {
        var statusMap = supEntry.records || {};
        return statusMap[status] || [];
    }

    /**
     * Returns the records that are selected using the checkboxes.
     * @returns {Array}
     */
    function getSelectedRecords(status, omitOwnRecords) {
        var selectedRecords = [];
        var selectedSup = $scope.getSelSupEntry();
        var statusRecords = getRecords(selectedSup, status);
        var selectedIndices = $scope.state.selectedIndices[status];
        for (var index in selectedIndices) {
            if (!selectedIndices.hasOwnProperty(index) || !selectedIndices[index]) {
                continue;
            }
            if (!statusRecords.hasOwnProperty(index)) {
                console.error('selected index does\'t exist!',
                              'index:', index, 'record_status_list', statusRecords, 'supervisor', selectedSup);
                continue;
            }
            var record = statusRecords[index];
            if (omitOwnRecords && record.employeeId === $scope.state.userEmpId) {
                continue;
            }
            selectedRecords.push(record);
        }
        return selectedRecords;
    }

    /**
     * Takes in an object with two sets: one of approved records, one of disapproved records
     * Modifies the record status accordingly for each record in these sets
     * Submits all passed in records via the API
     */
    function submitReviewedRecords(reviewedRecords) {
        var recordsToSubmit = [];
        angular.forEach(reviewedRecords.approved, function(record) {
            record.action = "submit";
            recordsToSubmit.push(record);
        });
        angular.forEach(reviewedRecords.disapproved, function(record) {
            record.action = "reject";
            record.remarks = record.rejectionRemarks;
            recordsToSubmit.push(record);
        });
        if (recordsToSubmit.length > 0) {
            submitRecords(recordsToSubmit);
        } else {
            console.log('no records to submit');
        }
    }

    /**
     * Unselects all selected record indices
     */
    function resetSelection() {
        $scope.state.selectedIndices = angular.copy(initialSelectedIndices);
    }

}
