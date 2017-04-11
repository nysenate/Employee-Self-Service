angular.module('ess')
    .filter('possessive', possessiveFilter);

/**
 * Converts the given string (hopefully a noun) into a possessive noun
 * @returns {Function}
 */
function possessiveFilter () {
    return function (noun) {
        if (!(typeof noun === 'string' || noun instanceof String)) {
            console.error('Passed non-string value into possessive filter:', noun);
            return noun;
        }
        if (noun.length === 0) {
            console.warn('Passed empty string into possessive filter');
            return noun;
        }
        var possessiveNoun = noun + "'";
        if (noun.charAt(noun.length - 1).toLowerCase() !== 's') {
            possessiveNoun += 's';
        }
        return possessiveNoun;
    }
}