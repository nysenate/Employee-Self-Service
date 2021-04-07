/**
 * Attribute directive that executes an expression when the element scrolls to the bottom
 */
angular.module('ess')
    .directive('onScrollToBottom', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var offset = 0;
                var offsetAttr = attrs['onScrollToBottomOffset'];
                if (offsetAttr && !isNaN(offsetAttr)) {
                    offset = parseInt(offsetAttr);
                }

                var raw = element[0];

                element.bind('scroll', function () {
                    // console.log('yo', raw.scrollTop, raw.offsetHeight, raw.scrollHeight, offset);
                    if (raw.scrollTop + raw.offsetHeight >= raw.scrollHeight - offset) {
                        console.log('scrolled to bottom!');
                        scope.$apply(attrs.onScrollToBottom);
                    }
                });
            }
        };
    });