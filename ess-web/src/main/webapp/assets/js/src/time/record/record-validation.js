var essTime = angular.module('essTime');

/** Validate the record entry when changing rows.
 * attrs: method for validating record. */
essTime.directive('recordValidator', ['$timeout', 'activeRow', function($timeout, activeRow) {
    return {
        restrict: 'A',
        link: function(scope, tbody, attrs) {
            $timeout(function() { // timeout makes this directive execute after table has been generated.
                tbody.children().each(function(index) {
                    $(this).on('focusin', function(event) {
                        if (newRowInFocus(index)) {
                            scope.$broadcast('validateRecordEntries');
                            scope.$apply(attrs.recordValidator);
                        }
                        activeRow.setActiveRow(index);
                    });
                });
            });

            function newRowInFocus(index) {
                return activeRow.getActiveRow() !== null && index !== activeRow.getActiveRow()
            }
        }
    }
}]);

/** Adds invalid class to a input if its invalid. Only run when 'validateRecordEntries' event is broadcast. */
essTime.directive('entryValidator', ['$timeout', function($timeout) {
    return {
        restrict: 'A',
        link: function (scope, element, attrs) {
            scope.$on('validateRecordEntries', function (event, args) {
                $timeout(function(){ // Run on next digest. Needed since its called from within a digest when save/submit btn clicked.
                    if (!scope.$apply(attrs.entryValidator)){
                        element.addClass('invalid');
                    }
                    else {
                        element.removeClass('invalid');
                    }
                }, 0, false);

            });
        }
    }
}]);

/** Keep track of the active time entry row so we can find out when it changes. */
essTime.service('activeRow', [function() {
    var activeRow = null;
    return {
        getActiveRow: function() {
            return activeRow;
        },
        setActiveRow: function(row) {
            activeRow = row;
        }
    }
}]);
