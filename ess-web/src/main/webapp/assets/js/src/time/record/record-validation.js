var essTime = angular.module('essTime');
var binding = 0;

/** Validate the record entry when changing rows.
 * attrs: method for validating record. */
essTime.directive('recordValidator', ['$timeout', '$rootScope', function($timeout, $rootScope) {
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
                        var element = angular.element($elem.children()[index]);
                        function callback (event) {
                            $rootScope.$emit('validateRecordEntries');
                            $scope.validate();
                            $rootScope.$digest()
                        }
                        element.on('focusout', callback);
                    });
                });
            });
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
            function validateEntry(event, args) {
                $timeout(function () { // Run on next digest. Needed since its called from within a digest when save/submit btn clicked.
                    if (!$scope.validate($scope.entry)){
                        $element.addClass('invalid');
                    }
                    else {
                        $element.removeClass('invalid');
                    }
                    $rootScope.$digest();
                }, 0, false);
            }
            var deregisterValidateEntry = $rootScope.$on('validateRecordEntries', validateEntry);
            $scope.$on('$destroy', function () {
                deregisterValidateEntry();
            });
        }
    }
}]);
