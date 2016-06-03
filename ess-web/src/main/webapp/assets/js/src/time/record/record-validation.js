var essTime = angular.module('essTime');
var binding = 0;

/** Validate the record entry when changing rows.
 * attrs: method for validating record. */
essTime.directive('recordValidator', ['$timeout', '$rootScope', 'activeTimeEntryRow', function($timeout, $rootScope, activeRow) {
    return {
        restrict: 'A',
        scope: {
            validateRecord: '&validate',
            record: '='
        },
        link: function($scope, $elem, $attrs) {
            // Every time a new record is selected, set validation functions to hook on entry focus
            $scope.$watch('record.timeRecordId', function() {
                $timeout(function () { // timeout makes this directive execute after table has been generated.
                    var validatePending = false;  // Set to true if a validation is pending
                    $elem.children().each(function (index) {
                        function focusInCallBack (event) {
                            activeRow.setActiveRow(index);
                            validate();
                        }
                        function focusOutCallback (event) {
                            activeRow.setActiveRow(null);
                            validate();
                        }
                        function validate () {
                            // Set a flag to indicate that validation was triggered
                            // If validate was invoked by a focus in and focus out
                            //  then the validations will only be performed once on the next digest
                            validatePending = true;
                            $timeout(function () {
                                if (validatePending) {
                                    // Timeout so that the record scope validation is performed on 
                                    //  the same digest as the entry validation
                                    $scope.validateRecord();
                                    $rootScope.$emit('validateRecordEntries');
                                    validatePending = false;
                                }
                            });
                        }
                        var element = angular.element($elem.children()[index]);
                        element.on('focusin', focusInCallBack);
                        element.on('focusout', focusOutCallback);
                    });
                });
            });
        }
    }
}]);

/** Adds invalid class to a input if its invalid. Only run when 'validateRecordEntries' event is broadcast. */
essTime.directive('entryValidator', ['$timeout', '$rootScope',
function($timeout, $rootScope) {
    return {
        restrict: 'A',
        scope: {
            validate: '&'
        },
        link: function ($scope, $element, $attrs) {
            function validateEntry(event, args) {
                if (!$scope.validate()) {
                    $element.addClass('invalid');
                }
                else {
                    $element.removeClass('invalid');
                }
                $rootScope.$digest();
            }
            var deregisterValidateEntry = $rootScope.$on('validateRecordEntries', validateEntry);
            $scope.$on('$destroy', deregisterValidateEntry);
        }
    }
}]);

essTime.service('activeTimeEntryRow', function () {
    var activeRow = null;
    return {
        getActiveRow: function() {
            return activeRow;
        },
        setActiveRow: function(row) {
            activeRow = row;
        }
    }
});
