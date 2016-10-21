angular.module('essTime')
    .directive('recordValidator',
        ['$timeout', '$rootScope', 'debounce', 'activeTimeEntryRow', recordValidatorDirective])
    .directive('entryValidator', ['$timeout', '$rootScope', entryValidatorDirective])
    .service('activeTimeEntryRow', activeTimeEntryRowService);

/**
 * Validate the record entry when changing rows.
 * attrs: method for validating record.
 */
function recordValidatorDirective ($timeout, $rootScope, debounce, activeRow) {
    return {
        restrict: 'A',
        scope: {
            validateRecord: '&validate',
            record: '='
        },
        link: function($scope, $elem, $attrs) {
            function validate () {
                $scope.validateRecord();
                $rootScope.$emit('validateRecordEntries');
            }
            var debounceDelay = 350;
            var debouncedValidate = debounce(validate, debounceDelay);
            // Every time a new record is selected, set validation functions to hook on entry focus
            $scope.$watch('record.timeRecordId', function() {
                $timeout(function () { // timeout makes this directive execute after table has been generated.
                    $elem.children().each(function (index) {
                        function focusInCallBack (event) {
                            activeRow.setActiveRow(index);
                            debouncedValidate();
                        }
                        function focusOutCallback (event) {
                            activeRow.setActiveRow(null);
                            debouncedValidate();
                        }
                        var element = angular.element($elem.children()[index]);
                        element.on('focusin', focusInCallBack);
                        element.on('focusout', focusOutCallback);
                    });
                });
            });
        }
    }
}

/**
 * Adds invalid class to a input if its invalid.
 * Only run when 'validateRecordEntries' event is broadcast.
 */
function entryValidatorDirective($timeout, $rootScope) {
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
                // $rootScope.$digest();
            }
            var deregisterValidateEntry = $rootScope.$on('validateRecordEntries', validateEntry);
            $scope.$on('$destroy', deregisterValidateEntry);
        }
    }
}

function activeTimeEntryRowService() {
    var activeRow = null;
    return {
        getActiveRow: function() {
            return activeRow;
        },
        setActiveRow: function(row) {
            activeRow = row;
        }
    }
}
