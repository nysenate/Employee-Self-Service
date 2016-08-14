var essApp = angular.module('ess');

essApp.filter('moment', ['$filter', function($filter) {
    return function(input, format, defaultVal) {
        if (input) {
            return moment(input).format(format);
        }
        else {
            return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
        }
    };
}]);

// Returns a string indicating the input date's temporal distance from the current time e.g. a month ago, an hour from now
essApp.filter('momentFromNow', function () {
    return function(input, suffix, defaultVal) {
        suffix = suffix === true;
        if (input) {
            return moment(input).fromNow(suffix);
        }
        return (typeof defaultVal !== 'undefined') ? defaultVal : "--";
    };
});

// Allows date comparison using equals, less than, or greater than
essApp.filter('momentCmp', function () {
    return function (lhs, operator, rhs, precision) {
        if (rhs === 'now') {
            rhs = moment();
        }
        switch (operator) {
            case 'eq':case '=':
                return moment(lhs).isSame(rhs, precision);
            case 'gt':case '>':
                return moment(lhs).isAfter(rhs, precision);
            case 'lt':case '<':
                return moment(lhs).isBefore(rhs, precision);
            default:
                console.error('invalid moment comparison operator', operator);
                return false;
        }
    };
});

// Tests if the (numerical 0-6) day of the week of the given moment is equal to one of the specified days of week
essApp.filter('momentIsDOW', function () {
    return function (date, daysOfWeek) {
        if (daysOfWeek.constructor !== Array) {
            daysOfWeek = [daysOfWeek];
        }
        return daysOfWeek.indexOf(moment(date).day()) >= 0;
    };
});