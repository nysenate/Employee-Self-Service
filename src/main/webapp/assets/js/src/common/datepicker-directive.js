var essApp = angular.module('ess');

/**
 * Directive that wraps the jquery ui datepicker
 */
essApp.directive('datepicker', [function(){
    return {
        restrict: 'AE',
        scope: {
            stepMonths: "@",    // Set to false to disable month toggle.
            inline: "@",        // Set true if datepicker should be inline
            defaultDate: "@",   // Default Date to display
            beforeShowDay: "&?",// Pass a custom day preprocessor
                                // See http://api.jqueryui.com/datepicker/#option-beforeShowDay
            // If this date picker is one of two date pickers to select a date range,
            //  the date of the other picker can be bound to the toDate or fromDate
            //  to restrict dates that would result in an invalid range
            fromDate: "=?",     // Overrides toDate if picked
            toDate: "=?"
        },
        link: function ($scope, $element, $attrs) {

            var defaultDate = ($scope.defaultDate) ? $scope.defaultDate : new Date();

            var customBeforeShowDay = undefined;
            if (typeof $scope.beforeShowDay === 'function') {
                customBeforeShowDay = $scope.beforeShowDay();
            }

            /**
             * Determine if the given date is selectable within
             * the constraints defined by the fromDate or toDate parameters
             * @param date
             * @returns boolean
             */
            function validDay(date) {
                if ($scope.fromDate) {
                    return !moment($scope.fromDate).isAfter(date)
                } else if ($scope.toDate) {
                    return !moment($scope.toDate).isBefore(date)
                }
                return true;
            }

            /**
             * Custom day preprocessor function
             * Returns the passed in custom function if available
             * Filters selectable dates based on date range constraints
             * @param date
             */
            function beforeShowDay (date) {
                var beforeShowDayResult = [true];
                if (typeof customBeforeShowDay === 'function') {
                    beforeShowDayResult = customBeforeShowDay(date);
                }
                beforeShowDayResult[0] = beforeShowDayResult[0] && validDay(date);
                return beforeShowDayResult;
            }

            $element.datepicker({
                showOn: "button",
                buttonImage: "/assets/img/calendar.png",
                buttonImageOnly: true,
                inline: $scope.inline || false,
                stepMonths: $scope.stepMonths || 1,
                defaultDate: defaultDate,
                beforeShowDay: beforeShowDay
            });

            if ($scope.stepMonths === "false") {
                $element.find(".ui-datepicker-prev, .ui-datepicker-next").remove();
            }
        }
    }
}]);