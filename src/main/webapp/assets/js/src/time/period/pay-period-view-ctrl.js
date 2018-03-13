var essTime = angular.module('essTime');

essTime.controller('PayPeriodCalendarCtrl',
    ['$scope', '$http', 'PayPeriodApi', 'HolidayApi', 'modals',
    function($scope, $http, PayPeriodApi, HolidayApi, modals) {

    $scope.state = {
        year: moment().year(),
        currentDayMoment: moment().startOf('day')
    };
    $scope.yearList = Array.apply(0, Array(10)).map(function (x, y) { return (($scope.state.year + 2) - y - 1); });
    $scope.months = [];
    $scope.periods = [];

    $scope.getPayPeriods = function(year, callback) {
        $scope.periodResp = PayPeriodApi.get({
            periodType: 'AF', year: year
        }, function() {
            $scope.periods = $scope.periodResp.periods;
            $scope.periodMap = $scope.periods.reduce(function (res, curr) {
                res[curr.endDate] = curr;
                return res;
            }, {});
            if (callback) callback();
        }, $scope.handleErrorResponse);
    };

    $scope.getHolidays = function(year, callback) {
        $scope.holidaysResp = HolidayApi.get({year: year}, function() {
            $scope.holidays = $scope.holidaysResp.holidays;
            $scope.holidayMap = $scope.holidays.reduce(function(res, curr) { res[curr.date] = curr; return res; }, {});
            if (callback) callback();
        });
    };

    $scope.generateMonths = function(year) {
        $scope.months = [];
        for (var i = 0; i < 12; i++) {
            $scope.months.push(moment().year(year).month(i).format('M/D/YYYY'));
        }
    };

    $scope.$watch('state.year', function(year, oldYear) {
        $scope.getPayPeriods(year, function() {
            $scope.getHolidays(year, function() {
                $scope.generateMonths(year);
            });
        });
    });

    /**
     * Method to call for 'beforeShowDate' on the datepicker. This will mark the pay period
     * dates and other relevant dates with a specific class so that they are highlighted.
     * @returns {Function}
     */
    $scope.periodHighlight = function() {
        return function(date) {
            var cssClasses = [];
            var toolTips = [];
            var mDate = moment(date).startOf('day');
            var mDateStr = mDate.format('YYYY-MM-DD');
            if (mDate.isSame($scope.state.currentDayMoment)) {
                cssClasses.push('current-date');
            }
            if (mDate.day() == 6 || mDate.day() == 0) {
                cssClasses.push('weekend-date');
            }
            else {
                if ($scope.holidayMap[mDateStr]) {
                    toolTips.push($scope.holidayMap[mDateStr]['name']);
                    cssClasses.push('holiday-date');
                }
                if ($scope.periodMap[mDateStr] && !$scope.periodMap[mDateStr].endYearSplit) {
                    toolTips.push('Last Day of Pay Period ' + $scope.periodMap[mDateStr]['payPeriodNum']);
                    cssClasses.push('pay-period-end-date');
                }
            }
            return [false, cssClasses.join(' '), toolTips.join(' \\ ')];
        }
    }
}]);