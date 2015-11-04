var essTime = angular.module('essTime');

essTime.controller('RecordHistoryCtrl', ['$scope', 'appProps', '$q', 'ActiveYearsTimeRecordsApi',
                                         'TimeRecordsApi', 'modals', 'RecordUtils',
function ($scope, appProps, $q, ActiveYearsTimeRecordsApi, timeRecordsApi, modals, recordUtils) {

    $scope.state = {
        searching: false,
        recordYears: null,
        year: null
    };

    $scope.init = function() {
        var empId = appProps.user.employeeId;
        ActiveYearsTimeRecordsApi.get({empId: empId}, function(resp) {
            if (resp.success) {
                $scope.state.recordYears = resp.years.reverse();
                $scope.state.year = $scope.state.recordYears[0];
                $scope.getRecords();
            }
            else {
                modals.open('500', {action: 'Get time record history', details: resp});
            }
        });
    };

    // Settings for floating the time entry table heading
    $scope.floatTheadOpts = {
        scrollingTop: 47
    };

    /** --- API Methods --- */

    // Get all records for the current user for the selected year
    $scope.getRecords = function() {
        $scope.state.searching = true;
        $scope.records = {employee: [], other: []};
        $scope.annualTotals = {};
        var empId = appProps.user.employeeId;
        var now = moment();
        var params = {
            empId: empId,
            from: $scope.state.year.toString() + '-01-01',
            to: $scope.state.year < now.year() ?
                $scope.state.year.toString() + '-12-31' :
                now.format('YYYY-MM-DD')
        };
        timeRecordsApi.get(params, function(response) {
            initializeRecords(response.result.items[empId]);
            $scope.state.searching = false;
            // Show historical records in reverse chrono order.
            $scope.records.other.reverse();
        }, function(response) {
            $scope.state.searching = false;
            modals.open('500', {details: response});
            console.log(resp);
        })
    };

    /** --- Display Methods --- */

    // Open a new modal window showing a detailed view of the given record
    $scope.showDetails = function(record) {
        var params = { record: record };
        modals.open('record-details', params);
    };

    /** --- Internal Methods --- */

    // Calculate totals for each record from a record response and categorize them by scope
    function initializeRecords(responseRecords) {
        for(var i in responseRecords) {
            var record = responseRecords[i];

            recordUtils.calculateDailyTotals(record);
            record.totals = recordUtils.getRecordTotals(record);

            if (record.scope === "E") {
                $scope.records.employee.push(record);
            } else {
                addToAnnualTotals(record);
                $scope.records.other.push(record);
            }
        }
    }

    // Add the totals of the given record to the running annual totals
    function addToAnnualTotals(record) {
        for(var field in record.totals) {
            if (record.totals.hasOwnProperty(field)) {
                if (!$scope.annualTotals.hasOwnProperty(field)) {
                    $scope.annualTotals[field] = 0;
                }
                $scope.annualTotals[field] += record.totals[field];
            }
        }
    }

    $scope.init();
}]);
