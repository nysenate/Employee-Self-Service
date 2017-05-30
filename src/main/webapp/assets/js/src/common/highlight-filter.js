
angular.module('ess')
    .filter('highlight', ['$sce', '$sanitize', highlightFilter])
;

/**
 * A filter that highlights text that matches a given term
 * @param $sce
 * @param $sanitize
 * @returns {Function}
 */
function highlightFilter($sce, $sanitize) {

    var highlightedClass = 'highlight-filter-highlighted';
    var highlightTemplate = '<span class="' + highlightedClass + '">$1</span>';

    return function (text, term) {
        if (!term) {
            return text;
        }

        if (typeof text !== 'string') {
            console.warn('Attempt to highlight a non-string value: ', text);
            return text;
        }

        text = $sanitize(text);

        text = text.replace(new RegExp('(' + term + ')', 'gi'), highlightTemplate);

        return $sce.trustAsHtml(text);
    }
}

