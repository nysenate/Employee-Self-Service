
/**
 * A filter that formats the given INTEGER number by padding it with zeros
 * Zeros will be appended to the front of the number so that the formatted string is a certain length
 */
angular.module('ess')
    .filter('zeroPad', zeroPadFilter);

function zeroPadFilter() {
    return zeroPad;
}

/**
 * Formats the given integer to the given length by appending zeros to the front
 *
 * @param number - an integer
 * @param length - desired minimum length of the number
 */
function zeroPad(number, length) {
    var numberString = "" + number;
    if (numberString.length > length) {
        return numberString;
    }
    return (Math.pow(10, length) + "" + numberString).slice(-length);
}
