var essTime = angular.module('essTime');

/** --- Filters --- */

essTime.filter('timeRecordStatus', ['$sce', function($sce) {
    var statusDispMap = {
        NOT_SUBMITTED: ["Not Submitted", "#444444"],
        SUBMITTED: ["Submitted", "#0e4e5a"],
        DISAPPROVED: ["Supervisor Disapproved", "#B90504"],
        APPROVED: ["Supervisor Approved", "#799933"],
        DISAPPROVED_PERSONNEL: ["Personnel Disapproved", "#B90504"],
        SUBMITTED_PERSONNEL: ["Submitted Personnel", "#808d0a"],
        APPROVED_PERSONNEL: ["Personnel Approved", "#799933"]
    };
    return function (status, showColor) {
        var statusDisp = (statusDispMap.hasOwnProperty(status)) ? statusDispMap[status][0] : "Unknown Status";
        var color = (statusDispMap.hasOwnProperty(status)) ? statusDispMap[status][1] : "red";
        if (showColor) {
            return $sce.trustAsHtml("<span style='color:" + color + "'>" + statusDisp + "</span>");
        }
        return statusDisp;
    };
}]);

// Returns a display label for the given misc leave id
essTime.filter('miscLeave', ['appProps', function (appProps) {
    var miscLeaveMap = {};
    angular.forEach(appProps.miscLeaves, function (miscLeave) {
        miscLeaveMap[miscLeave.type] = miscLeave;
    });
    return function (miscLeave, defaultLabel) {
        if (miscLeaveMap.hasOwnProperty(miscLeave)) {
            return miscLeaveMap[miscLeave].shortName;
        }
        if (!miscLeave) {
            return defaultLabel ? defaultLabel : '--';
        }
        return miscLeave + "?!";
    };
}]);

/**
 * Colors a number based on whether it's positive or negative to provide a
 * visual cue.
 *
 * Example,
 * given 7 -> +7 (green)
 * given -3 -> -3 (red)
 * given 0 -> 0 (default color)
 */
essTime.filter('hoursDiffHighlighter', ['$sce', function($sce) {
    return function (hours) {
        var color = '#0e4e5a';
        var sign = '';
        if (hours > 0) {
            color = '#09BB05';
            sign = '+';
        }
        else if (hours < 0) {
            color = '#BB0505';
        }
        return $sce.trustAsHtml('<span style="color:' + color + '">' + sign + hours + '</span>');
    }
}]);
