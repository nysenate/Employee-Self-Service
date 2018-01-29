/**
 * Attribute directive that executes an expression when the element scrolls to the bottom
 */
angular.module('ess')
    .directive('onScrollToBottom', function () {
        return {
            restrict: 'A',
            link: function (scope, element, attrs) {
                var raw = element[0];

                element.bind('scroll', function () {
                    if (raw.scrollTop + raw.offsetHeight > raw.scrollHeight) {
                        scope.$apply(attrs.onScrollToBottom);
                    }
                });
            }
        };
    });