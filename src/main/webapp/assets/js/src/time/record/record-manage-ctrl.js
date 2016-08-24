var essApp = angular.module('ess');

essApp.controller('RecordManageCtrl', ['$scope', '$q', 'appProps', 'RecordUtils', 'modals', 'badgeService',
    'SupervisorTimeRecordsApi', 'TimeRecordApi', 'TimeRecordReminderApi',
    recordManageCtrl]);

function recordManageCtrl($scope, $q, appProps, recordUtils, modals,
                          badgeService, supRecordsApi, timeRecordsApi, reminderApi) {
    $scope.state = {
        // Data
        supIds: {},
        empInfos: {},   // Mapping of empId -> employee data
        supRecords: {},

        // Page state
        loading: false,     // If data is being fetched
        selSupId: null,     // The currently selected id from the supIds map
        selectedIndices: {  // Mapping of selected record indices by status code, e,g {'SUBMITTED' : { 1:true, 2:true }}
            NOT_SUBMITTED: {},         // TODO: Turn this into a constant
            SUBMITTED: {},
            DISAPPROVED: {},
            APPROVED: {},
            DISAPPROVED_PERSONNEL: {},
            SUBMITTED_PERSONNEL: {},
            APPROVED_PERSONNEL: {}
        }
    };

    // This key is used for grouping all records under a single item regardless of supervisor.
    var allSupervisorsId = 'all';

    function setDefaultValues() {
        $scope.state.selSupId = allSupervisorsId;
        $scope.state.supIds = [allSupervisorsId];
        $scope.state.empInfos = {};
        $scope.state.supRecords = {};
    }

    $scope.init = function () {
        getEmployeeActiveRecords();
    };

    /** --- Api Methods --- */

    /**
     * Retrieves all active, in progress records that are under supervision of the current user
     * Also gets employee infos for the supervisor, any overridden supervisor, and all employees in the returned records
     */
    function getEmployeeActiveRecords() {
        $scope.state.loading = true;
        setDefaultValues();
        var from = moment().subtract(1, 'year').format('YYYY-MM-DD');
        var to = moment().format('YYYY-MM-DD');
        var empId = appProps.user.employeeId;
        supRecordsApi.get({supId: empId, from: from, to: to}, function onSuccess(resp) {
            initializeRecords(resp.result.items);
            updateRecordsPendingBadge();
            resetSelection();
            $scope.state.loading = false;
        }, function onFail(resp) {
            $scope.state.loading = false;
            modals.open('500', {details: resp});
            console.log(resp);
        });
    }

    /**
     * Gets the total number of time records that require action and updates any badges that displays
     * this count.
     */
    function updateRecordsPendingBadge() {
        var submitted = ($scope.state.supRecords[allSupervisorsId].SUBMITTED) ?
            $scope.state.supRecords[allSupervisorsId].SUBMITTED.length : 0;
        badgeService.setBadgeValue('pendingRecordCount', submitted);
    }

    /**
     * Submits each record of an array of records
     * Refreshes record data after each record has been submitted
     */
    function submitRecords (records) {
        var promises = [];
        records.forEach(function(record) {
            promises.push(timeRecordsApi.save({action: record.action}, record,
                function(response){console.log('success:', record.timeRecordId, response)},
                function(response){console.log('fail:', record.timeRecordId, response); return response;}
            ).$promise);
        });
        $scope.state.loading = true;
        return $q.all(promises)
            .then(function onFulfilled() {
                console.log(records.length, 'records submitted');
                getEmployeeActiveRecords();
            }, function onRejected(resp) {
                $scope.state.loading = false;
                modals.open('500', {details: resp});
                console.log(resp);
            });
    }

    /** --- Display Methods --- */

    /**
     * Generates an option label for the given supervisor id
     * not the angular way, but you try fitting this into an ng-options
     */
    $scope.getOptionLabel = function(supId) {
        return (supId == allSupervisorsId ? 'All Supervisors' : $scope.state.empInfos[supId].fullName) +
            ' - (' + ($scope.state.supRecords[supId].SUBMITTED || []).length + ' Pending Records)';
    };

    /**
     * Returns true if there are multiple supervisors to approve records for,
     *  i.e. the current supervisor has been granted an override
     */
    $scope.multipleSups = function() {
        return Object.keys($scope.state.supRecords).length > 2;
    };

    /**
     * Causes all SUBMITTED records to be selected for review
     */
    $scope.selectAll = function(status) {
        for(var i = 0; i < $scope.state.supRecords[$scope.state.selSupId][status].length; i++) {
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
        var selectedRecords = getSelectedRecords(status);
        var params = {
            records: selectedRecords,
            allowApproval: allowApproval
        };
        modals.open('record-review', params, true)
            .then(submitReviewedRecords)
            .then($scope.selectNone);
    };

    $scope.hasSelections = function(status) {
        for (var p in $scope.state.selectedIndices[status]) {
            if ($scope.state.selectedIndices[status].hasOwnProperty(p) && $scope.state.selectedIndices[status][p] === true) {
                return true;
            }
        }
        return false;
    };

    $scope.submitPrompt = function(records) {
        var params = {approved: records};
        return modals.open('record-approval-submit', params);
    };

    /**
     * Submits all displayed records that are awaiting supervisor approval as 'APPROVED'
     */
    $scope.approveSelections = function () { var selectedRecords = getSelectedRecords('SUBMITTED');
        if (selectedRecords) {
            selectedRecords.forEach(function (record) {
                record.action = "submit";
            });
            $scope.submitPrompt(selectedRecords)
                .then(function() {
                    console.log("meow");
                    submitRecords(selectedRecords);
                });
        }
    };

    $scope.remindSelections = function (status) {
        var selectedRecords = getSelectedRecords(status);
        modals.open('record-reminder-prompt', {records: selectedRecords}, true)
            .then(function () {
                return postReminders(selectedRecords);
            })
            .then(function () {
                modals.open('record-reminder-posted', true)
            });
    };

    $scope.resolveModal = modals.resolve;

    function postReminders(timeRecords) {
        if (!timeRecords) {
            return $q.when();
        }
        var empIds = [];
        var beginDates = [];
        timeRecords.forEach(function (record) {
            empIds.push(record.employeeId);
            beginDates.push(record.beginDate);
        });
        modals.open('record-reminder-posting');
        return reminderApi.save({empId: empIds, beginDate: beginDates}, {}, function() {}, 
            function(errorData) {
                console.error('reminder post', errorData);
                modals.rejectAll();
                modals.open('500', errorData);
            }).$promise
            .finally(modals.resolve);
    }

    /** --- Internal Methods --- */

    /**
     * Calculates totals for each given record
     * Puts record into $scope.supRecords keyed by supervisor id and record status
     * Returns a list of employee ids for which we don't yet have employee infos
     */
    function initializeRecords(recordMap) {
        // All records are stored under an additional supId to facilitate access to all records regardless of supervisor
        var allRecords = $scope.state.supRecords[allSupervisorsId] = {};
        angular.forEach(recordMap, function(records, empId) {
            angular.forEach(records, function(record) {
                // Compute totals for the record
                recordUtils.calculateDailyTotals(record);
                record.totals = recordUtils.getRecordTotals(record);
                // Store the record under the supervisor, grouped by status code
                var currSupId = record.supervisorId;
                var supIdList = $scope.state.supIds;
                if (supIdList.indexOf(currSupId) == -1) {
                    $scope.state.supIds.push(currSupId);
                    $scope.state.empInfos[currSupId] = record.supervisor;
                }
                var currSupRecords = $scope.state.supRecords[currSupId] = $scope.state.supRecords[currSupId] || {};
                var statusList = currSupRecords[record.recordStatus] = currSupRecords[record.recordStatus] || [];
                var allStatusList = allRecords[record.recordStatus] = allRecords[record.recordStatus] || [];
                statusList.push(record);        // Store under supervisor
                allStatusList.push(record);     // Store under all
            });
        });
    }

    /**
     * Returns the records that are selected using the checkboxes.
     * @returns {Array}
     */
    function getSelectedRecords(status) {
        var selectedRecords = [];
        for (var index in $scope.state.selectedIndices[status]) {
            if ($scope.state.selectedIndices[status].hasOwnProperty(index)) {
                selectedRecords.push($scope.state.supRecords[$scope.state.selSupId][status][index]);
            }
        }
        return selectedRecords;
    }

    /**
     * Takes in an object with two sets: one of approved records, one of disapproved records
     * Modifies the record status accordingly for each record in these sets
     * Submits all passed in records via the API
     */
    function submitReviewedRecords(reviewedRecords) {
        console.log('review modal resolved:', reviewedRecords);
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
        angular.forEach($scope.state.selectedIndices, function (selections, status) {
            $scope.selectNone(status);
        });
    }

    $scope.init();
}
