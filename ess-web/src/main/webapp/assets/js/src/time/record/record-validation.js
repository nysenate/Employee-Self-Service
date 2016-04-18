var essTime = angular.module('essTime');
var binding = 0;

/** Validate the record entry when changing rows.
 * attrs: method for validating record. */
essTime.directive('recordValidator', ['$timeout', '$rootScope', 'activeRow', function($timeout, $rootScope, activeRow) {
    return {
        restrict: 'A',
        scope: {
            validate: '&',
            record: '='
        },
        link: function($scope, $elem, $attrs) {
            // Every time a new record is selected, set validation functions to hook on entry focus
            $scope.$watch('record.timeRecordId', function() {
                $timeout(function () { // timeout makes this directive execute after table has been generated.
                    $elem.children().each(function (index) {
                        var bid = ++binding;
                        angular.element($elem.children()[index])
                            .on('focusin', function (event) {
                                if (newRowInFocus(index)) {
                                    $rootScope.$broadcast('validateRecordEntries');
                                    $scope.validate();
                                }
                                activeRow.setActiveRow(index);
                                $scope.$parent.$digest()
                            });
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
essTime.directive('entryValidator', ['$timeout', '$rootScope', function($timeout, $rootScope) {
    return {
        restrict: 'A',
        scope: {
            validate: '&'
        },
        link: function ($scope, $element, $attrs) {
            $rootScope.$on('validateRecordEntries', function (event, args) {
                $timeout(function(){ // Run on next digest. Needed since its called from within a digest when save/submit btn clicked.
                    if (!$scope.validate($scope.entry)){
                        $element.addClass('invalid');
                    }
                    else {
                        $element.removeClass('invalid');
                    }
                    $scope.$parent.$digest();
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
