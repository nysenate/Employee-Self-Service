var essApp = angular.module('ess');

/**  TODO: Make this browser compatible.  Doesn't work with current chrome version (50.0.2661.102) */

essApp.directive('textAutoHeight', ['$timeout', function ($timeout) {
    return {
        restrict: 'A',
        scope: { text: '=' },
        link: function($scope, $elem, $attrs) {
            $timeout(function() {
                console.warn("textAutoHeight directive may be incompatible with some browsers");
                var minHeight = $elem[0].offsetHeight,
                    paddingLeft = parseInt($elem.css('paddingLeft')) || 0,
                    paddingRight = parseInt($elem.css('paddingRight')) || 0;
                var $shadow = angular.element('<div></div>').css({
                    position: 'absolute',
                    top: -10000,
                    left: -10000,
                    width: $elem[0].offsetWidth - paddingLeft - paddingRight,
                    fontSize: $elem.css('fontSize'),
                    fontFamily: $elem.css('fontFamily'),
                    lineHeight: $elem.css('lineHeight'),
                    resize: 'none'
                });
                $scope.$watch(
                    function () {return $elem[0].offsetWidth;}, 
                    function(newWidth, oldWidth) {
                        if (newWidth !== oldWidth) {
                            $timeout(function () {
                                var paddingLeft = parseInt($elem.css('paddingLeft')) || 0,
                                    paddingRight = parseInt($elem.css('paddingRight')) || 0;
                                $shadow.css('width', newWidth - paddingLeft - paddingRight);
                            }, 50);
                        }
                    }
                );
                angular.element(document.body).append($shadow);

                var update = function () {
                    var times = function (string, number) {
                        for (var i = 0, r = ''; i < number; i++) {
                            r += string;
                        }
                        return r;
                    };

                    var val = $elem.val().replace(/</g, '&lt;')
                        .replace(/>/g, '&gt;')
                        .replace(/&/g, '&amp;')
                        .replace(/\n$/, '<br/>&nbsp;')
                        .replace(/\n/g, '<br/>')
                        .replace(/\s{2,}/g, function (space) {
                            return times('&nbsp;', space.length - 1) + ' '
                        });
                    $shadow.html(val);

                    $elem.css('height', Math.max($shadow[0].offsetHeight + 10 /* the "threshold" */, minHeight) + 'px');
                };

                $elem.bind('keyup keydown keypress change', update);
                update();
            }, 100);
        }
    }
}]);
