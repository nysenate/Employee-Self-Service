var essApp = angular.module('ess');

essApp.directive('datepicker', [function(){
    return {
        restrict: 'AE',
        scope: {
            stepMonths: "@",    // Set to false to disable month toggle.
            inline: "@",        // Set true if datepicker should be inline
            defaultDate: "@",   // Default Date to display
            beforeShowDay: "&"  // See http://api.jqueryui.com/datepicker/#option-beforeShowDay
        },
        link: function($scope, element, attrs) {

            var defaultDate = ($scope.defaultDate) ? $scope.defaultDate : new Date();

            element.datepicker({
                inline: $scope.inline || false,
                stepMonths: $scope.stepMonths || 1,
                defaultDate: defaultDate,
                beforeShowDay: $scope.beforeShowDay()
            });

            if ($scope.stepMonths === "false") {
                element.find(".ui-datepicker-prev, .ui-datepicker-next").remove();
            }
        }
    }
}]);