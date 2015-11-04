var essMyInfo = angular.module('essMyInfo');

/** Filter to capitalize first letter of each word and remove ':' characters*/
essMyInfo.filter('formatDeductionHeader', function() {
    return function(input, scope) {
        if (input !== null) {
            return input.replace(/\w\S*/g, function(txt) {
                txt = txt.replace(":", "");
                return txt.charAt(0).toUpperCase() + txt.substr(1).toLowerCase();
            });
        }
    }
});