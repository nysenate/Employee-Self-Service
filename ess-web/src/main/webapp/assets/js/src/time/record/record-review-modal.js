var essApp = angular.module('ess');

essApp.directive('recordReviewModal', ['appProps', 'modals', 'LocationService',
function (appProps, modals, locationService) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-review-modal',
        link: link
    };

    function link($scope, $elem, $attrs) {

        $scope.iSelectedRecord = 0;
        $scope.records = modals.params().records;
        $scope.allowApproval = modals.params().allowApproval;

        /**
         *  Records are categorized under approved or disapproved, keyed by time record id
         */
        var approved = {};
        var disapproved = {};

        /**
         * Add a custom fit class to this element so that it isn't sized in the default way
         */
        $elem.addClass('custom-fit');

        /** --- Display Methods --- */

        $scope.submitChanges = function() {
            console.log(approved);
            modals.open('record-approval-submit', {
                approved: approved,
                disapproved: disapproved
            }).then(function submitted() {
                $scope.resolve();
            });
        };

        /**
         * Resolves the modal, returning the records that were selected as approved/disapproved
         */
        $scope.resolve = function () {
            modals.resolve({
                approved: approved,
                disapproved: disapproved
            });
        };

        /**
         * Closes the modal without the intention of submitting records
         */
        $scope.close = modals.reject;

        /**
         * Removes the selected record from both the approved and disapproved categories
         */
        $scope.cancelRecord = function() {
            var record = $scope.records[$scope.iSelectedRecord];
            delete approved[record.timeRecordId];
            delete disapproved[record.timeRecordId];
        };

        /**
         * Adds the selected record to the 'approved' category
         */
        $scope.approveRecord = function () {
            var record = $scope.records[$scope.iSelectedRecord];
            $scope.cancelRecord();
            approved[record.timeRecordId] = record;
            selectNextPendingRecord();
        };

        /**
         * Opens a new modal to add rejection remarks for the selected record
         * If the modal is resolved, the record is added to the rejected category
         */
        $scope.rejectRecord = function () {
            var record = $scope.records[$scope.iSelectedRecord];
            modals.open('record-review-reject', {record: record})
                .then(function rejected(reasons) {
                    $scope.cancelRecord(record);
                    record.rejectionRemarks = reasons;
                    disapproved[record.timeRecordId] = record;
                    selectNextPendingRecord();
                });
        };

        /** Sets the given index as the index of the selected record */
        $scope.selectRecord = function (index) {
            $scope.iSelectedRecord = index;
        };

        $scope.next = function() {
            selectNextPendingRecord();
        };

        /**
         * Returns a string that indicates whether a record has been approved, disapproved or neither
         */
        $scope.getApprovalStatus = function(record) {
            if (record.timeRecordId in approved) {
                return 'approved';
            }
            if (record.timeRecordId in disapproved) {
                return 'disapproved';
            }
            return 'untouched';
        };

        $scope.submissionEmpty = function() {
            return Object.keys(approved).length === 0 && Object.keys(disapproved).length === 0;
        };

        /** --- Internal Methods --- */

        /**
         * Locates and selects the next pending record by searching after and then before the selected record;
         */
        function selectNextPendingRecord() {
            for (var i = 0; i < $scope.records.length; i++) {
                var iAdj = (i + $scope.iSelectedRecord) % $scope.records.length;
                if ($scope.getApprovalStatus($scope.records[iAdj]) === 'untouched') {
                    $scope.iSelectedRecord = iAdj;
                    locationService.scrollToId($scope.records[iAdj].timeRecordId);
                    return;
                }
            }
        }
    }
}]);

essApp.directive('recordReviewRejectModal', ['modals', 'appProps', function(modals, appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-reject-modal',
        link: function($scope, $elem, $attrs) {
            $scope.cancel = modals.reject;
            $scope.resolve = function() {
                modals.resolve($scope.remarks)
            };
        }
    };
}]);

essApp.directive('recordApproveSubmitModal', ['modals', 'appProps', function(modals, appProps) {
    return {
        templateUrl: appProps.ctxPath + '/template/time/record/record-approve-submit-modal',
        link: function($scope, $elem, $attrs) {
            $scope.approved = modals.params().approved;
            $scope.approvedCount = ($scope.approved) ? Object.keys($scope.approved).length : 0;
            $scope.disapproved = modals.params().disapproved;
            $scope.disapprovedCount = ($scope.disapproved) ? Object.keys($scope.disapproved).length : 0;

            $scope.cancel = modals.reject;
            $scope.resolve = function() {
                modals.resolve();
            };
        }
    }
}]);
