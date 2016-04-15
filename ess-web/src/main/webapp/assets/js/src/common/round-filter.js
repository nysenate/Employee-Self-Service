var essApp = angular.module('ess');

/**
 * Rounds the given number to a multiple of the given multiple.
 * Will round in the direction specified by 'direction'
 * @param number Number - the number to round
 * @param multiple Number - default 1 - the number will be rounded to a multiple of the absolute value of this
 * @param direction Number - default 0 - nearest if 0, down if < 0, up if > 0
 */
essApp.filter('round', function () {
    return function round(number, multiple, direction) {
        multiple = Math.abs(multiple || 1);
        direction = isNaN(direction) ? 0 : direction;

        var roundingFunction = Math.round;
        if (direction > 0){
            roundingFunction = Math.ceil;
        } else if (direction < 0) {
            roundingFunction = Math.floor;
        }

        var inverse = 1 / multiple;

        return roundingFunction(number * inverse) / inverse;
    }
});