var essApp = angular.module('ess');

essApp.directive('datepicker', [function(){
    return {
        restrict: 'AE',
        scope: {
            stepMonths: "@",    // Set to false to disable month toggle.
            inline: "@",        // Set true if datepicker should be inline
            defaultDate: "@",   // Default Date to display
            beforeShowDay: "="  // See http://api.jqueryui.com/datepicker/#option-beforeShowDay
        },
        link: function (scope, element, attrs) {

            var defaultDate = (scope.defaultDate) ? scope.defaultDate : new Date();

            var blackOutDate = function (date) {
                if (attrs.ngModel === "filter.date.from") {
                    var d1 = moment(date).format("MM/DD/YYYY");
                    var d2 = moment(scope.$parent.filter.date.to).format("MM/DD/YYYY");
                    return [d2 >= d1];
                }
                else {
                    var d1 = moment(date).format("MM/DD/YYYY");
                    var d2 = moment(scope.$parent.filter.date.from).format("MM/DD/YYYY");
                    return [d2 < d1];
                }
            };
            element.datepicker({
                inline: scope.inline || false,
                stepMonths: scope.stepMonths || 1,
                defaultDate: defaultDate,
                beforeShowDay: blackOutDate
            });

            if (scope.stepMonths === "false") {
                element.find(".ui-datepicker-prev, .ui-datepicker-next").remove();
            }
        }
    }
}]);